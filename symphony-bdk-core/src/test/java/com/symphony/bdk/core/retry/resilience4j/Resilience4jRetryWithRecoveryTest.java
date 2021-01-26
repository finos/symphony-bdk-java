package com.symphony.bdk.core.retry.resilience4j;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.retry.RecoveryStrategy;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.util.function.ConsumerWithThrowable;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Collections;

/**
 * Test class for {@link Resilience4jRetryWithRecovery}
 */
class Resilience4jRetryWithRecoveryTest {

  //to be able to use Mockito mocks around lambdas. Otherwise, does not work, even with mockito-inline
  private static class ConcreteSupplier implements SupplierWithApiException<String> {
    @Override
    public String get() throws ApiException {
      return "";
    }
  }


  private static class ConcreteConsumer implements ConsumerWithThrowable {
    @Override
    public void consume() {
    }
  }

  @Test
  void testSupplierWithNoExceptionReturnsValue() throws Throwable {
    String value = "string";

    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenReturn(value);

    Resilience4jRetryWithRecovery<String> r = new Resilience4jRetryWithRecovery<>("name",
        ofMinimalInterval(), supplier, (t) -> false,
        Collections.emptyList());

    assertEquals(value, r.execute());
    verify(supplier, times(1)).get();
  }

  @Test
  void testSupplierWithExceptionShouldRetry() throws Throwable {
    String value = "string";

    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get())
        .thenThrow(new ApiException(400, "error"))
        .thenReturn(value);

    Resilience4jRetryWithRecovery<String> r = new Resilience4jRetryWithRecovery<>("name",
        ofMinimalInterval(), supplier,
        (t) -> t instanceof ApiException && ((ApiException) t).isClientError(),
        Collections.emptyList());

    assertEquals(value, r.execute());
    verify(supplier, times(2)).get();
  }

  @Test
  void testSupplierWithExceptionAndNoRetryShouldFailWithException() throws Throwable {
    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(400, "error"));

    Resilience4jRetryWithRecovery<String> r = new Resilience4jRetryWithRecovery<>("name",
        ofMinimalInterval(), supplier,
        (t) -> false, Collections.emptyList());

    assertThrows(ApiException.class, () -> r.execute());
    verify(supplier, times(1)).get();
  }

  @Test
  void testMaxAttemptsReachedShouldFailWithException() throws ApiException {
    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(400, "error"));

    final BdkRetryConfig retryConfig = ofMinimalInterval();

    Resilience4jRetryWithRecovery<String> r = new Resilience4jRetryWithRecovery<>("name", retryConfig, supplier, (t) -> true,
        Collections.emptyList());

    assertThrows(ApiException.class, () -> r.execute());
    verify(supplier, times(retryConfig.getMaxAttempts())).get();
  }

  @Test
  void testExceptionNotMatchingRetryPredicateShouldBeForwarded() throws ApiException {
    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(400, "error"));

    Resilience4jRetryWithRecovery<String> r = new Resilience4jRetryWithRecovery<>("name",
        ofMinimalInterval(), supplier,
        (t) -> t instanceof ApiException && ((ApiException) t).isServerError(),
        Collections.emptyList());

    assertThrows(ApiException.class, () -> r.execute());
    verify(supplier, times(1)).get();
  }

  @Test
  void testIgnoredExceptionShouldReturnNull() throws Throwable {
    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(400, "error"));

    Resilience4jRetryWithRecovery<String> r = new Resilience4jRetryWithRecovery<>("name",
        ofMinimalInterval(), supplier, (t) -> true,
        (e) -> true, Collections.emptyList());

    assertNull(r.execute());
    verify(supplier, times(1)).get();
  }

  @Test
  void testMatchingExceptionShouldTriggerRecoveryAndRetry() throws Throwable {
    final String value = "string";

    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(400, "error")).thenReturn(value);

    ConcreteConsumer consumer = mock(ConcreteConsumer.class);

    Resilience4jRetryWithRecovery<String> r = new Resilience4jRetryWithRecovery<>("name",
        ofMinimalInterval(), supplier, (t) -> true,
        Collections.singletonList(new RecoveryStrategy(ApiException.class, e -> true, consumer)));

    assertEquals(value, r.execute());

    InOrder inOrder = inOrder(supplier, consumer);
    inOrder.verify(supplier).get();
    inOrder.verify(consumer).consume();
    inOrder.verify(supplier).get();
    verifyNoMoreInteractions(supplier, consumer);
  }

  @Test
  void testNonMatchingExceptionShouldNotTriggerRecoveryAndRetry() throws Throwable {
    final String value = "string";

    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(500, "error")).thenReturn(value);

    ConcreteConsumer consumer = mock(ConcreteConsumer.class);

    Resilience4jRetryWithRecovery<String> r = new Resilience4jRetryWithRecovery<>("name",
        ofMinimalInterval(), supplier, (t) -> true,
        Collections.singletonList(new RecoveryStrategy(ApiException.class, e -> e.isClientError(), consumer)));

    assertEquals(value, r.execute());
    verify(supplier, times(2)).get();
    verifyNoInteractions(consumer);
  }

  @Test
  void testThrowableInRecoveryAndNotMatchingRetryPredicateShouldBeForwarded() throws Throwable {
    final String value = "string";
    final ApiException error = new ApiException(400, "error");

    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(error).thenReturn(value);

    ConcreteConsumer consumer = mock(ConcreteConsumer.class);
    doThrow(new IndexOutOfBoundsException()).when(consumer).consume();

    Resilience4jRetryWithRecovery<String> r = new Resilience4jRetryWithRecovery<>("name",
        ofMinimalInterval(), supplier,
        (t) -> t instanceof ApiException,
        Collections.singletonList(new RecoveryStrategy(ApiException.class, ApiException::isClientError, consumer)));

    assertThrows(IndexOutOfBoundsException.class, () -> r.execute());

    InOrder inOrder = inOrder(supplier, consumer);
    inOrder.verify(supplier).get();
    inOrder.verify(consumer).consume();
    verifyNoMoreInteractions(supplier, consumer);
  }

  @Test
  void testThrowableInRecoveryAndMatchingRetryPredicateShouldLeadToRetry() throws Throwable {
    final String value = "string";
    final ApiException error = new ApiException(400, "error");

    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(error).thenReturn(value);

    ConcreteConsumer consumer = mock(ConcreteConsumer.class);
    doThrow(new IndexOutOfBoundsException()).when(consumer).consume();

    Resilience4jRetryWithRecovery<String> r = new Resilience4jRetryWithRecovery<>("name",
        ofMinimalInterval(), supplier,
        (t) -> true,
        Collections.singletonList(new RecoveryStrategy(ApiException.class, ApiException::isClientError, consumer)));

    assertEquals(value, r.execute());

    InOrder inOrder = inOrder(supplier, consumer);
    inOrder.verify(supplier).get();
    inOrder.verify(consumer).consume();
    inOrder.verify(supplier).get();
    verifyNoMoreInteractions(supplier, consumer);
  }

  @Test
  void testExecuteAndRetrySucceeds() throws Throwable {
    final String value = "string";

    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenReturn(value);

    assertEquals(value, Resilience4jRetryWithRecovery.executeAndRetry(new RetryWithRecoveryBuilder<String>(), "test", supplier, "serviceName"));
  }

  @Test
  void testExecuteAndRetryShouldConvertApiExceptionIntoApiRuntimeException() throws Throwable {
    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(500, ""));

    assertThrows(ApiRuntimeException.class,
        () -> Resilience4jRetryWithRecovery.executeAndRetry(new RetryWithRecoveryBuilder<String>(), "test", supplier, "serviceName"));
  }

  @Test
  void testExecuteAndRetryShouldConvertUnexpectedExceptionIntoRuntimeException() throws Throwable {
    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ArrayIndexOutOfBoundsException());

    assertThrows(RuntimeException.class,
        () -> Resilience4jRetryWithRecovery.executeAndRetry(new RetryWithRecoveryBuilder<String>(), "test", supplier, "serviceName"));
  }
}
