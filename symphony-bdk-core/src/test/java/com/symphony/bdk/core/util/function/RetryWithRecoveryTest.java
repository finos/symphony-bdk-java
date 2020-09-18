package com.symphony.bdk.core.util.function;

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

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.config.model.BdkRetryConfig;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Collections;

/**
 * Test class for {@link RetryWithRecovery}
 */
class RetryWithRecoveryTest {

  //to be able to use Mockito mocks around lambdas. Otherwise, does not work, even with mockito-inline
  private static class ConcreteSupplier implements SupplierWithApiException<String> {
    @Override
    public String get() throws ApiException {
      return "";
    }
  }


  private static class ConcreteConsumer implements ConsumerWithThrowable {
    @Override
    public void consume() throws Throwable {
      return;
    }
  }

  BdkRetryConfig getRetryConfig() {
    final BdkRetryConfig bdkRetryConfig = new BdkRetryConfig();
    bdkRetryConfig.setMultiplier(1);
    bdkRetryConfig.setInitialIntervalMillis(10);

    return bdkRetryConfig;
  }

  @Test
  void testSupplierWithNoExceptionReturnsValue() throws Throwable {
    String value = "string";

    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenReturn(value);

    RetryWithRecovery<String> r = new RetryWithRecovery<>("name", getRetryConfig(), supplier, (t) -> false,
        Collections.emptyMap());

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

    RetryWithRecovery<String> r = new RetryWithRecovery<>("name", getRetryConfig(), supplier,
        (t) -> t instanceof ApiException && ((ApiException) t).isClientError(),
        Collections.emptyMap());

    assertEquals(value, r.execute());
    verify(supplier, times(2)).get();
  }

  @Test
  void testSupplierWithExceptionAndNoRetryShouldFailWithException() throws Throwable {
    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(400, "error"));

    RetryWithRecovery<String> r = new RetryWithRecovery<>("name", getRetryConfig(), supplier,
        (t) -> false, Collections.emptyMap());

    assertThrows(ApiException.class, () -> r.execute());
    verify(supplier, times(1)).get();
  }

  @Test
  void testMaxAttemptsReachedShouldFailWithException() throws ApiException {
    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(400, "error"));

    final BdkRetryConfig retryConfig = getRetryConfig();

    RetryWithRecovery<String> r = new RetryWithRecovery<>("name", retryConfig, supplier, (t) -> true,
        Collections.emptyMap());

    assertThrows(ApiException.class, () -> r.execute());
    verify(supplier, times(retryConfig.getMaxAttempts())).get();
  }

  @Test
  void testExceptionNotMatchingRetryPredicateShouldBeForwarded() throws ApiException {
    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(400, "error"));

    RetryWithRecovery<String> r = new RetryWithRecovery<>("name", getRetryConfig(), supplier,
        (t) -> t instanceof ApiException && ((ApiException) t).isServerError(),
        Collections.emptyMap());

    assertThrows(ApiException.class, () -> r.execute());
    verify(supplier, times(1)).get();
  }

  @Test
  void testIgnoredExceptionShouldReturnNull() throws Throwable {
    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(400, "error"));

    RetryWithRecovery<String> r = new RetryWithRecovery<>("name", getRetryConfig(), supplier, (t) -> true,
        (e) -> true, Collections.emptyMap());

    assertNull(r.execute());
    verify(supplier, times(1)).get();
  }

  @Test
  void testMatchingExceptionShouldTriggerRecoveryAndRetry() throws Throwable {
    final String value = "string";

    SupplierWithApiException<String> supplier = mock(ConcreteSupplier.class);
    when(supplier.get()).thenThrow(new ApiException(400, "error")).thenReturn(value);

    ConcreteConsumer consumer = mock(ConcreteConsumer.class);

    RetryWithRecovery<String> r = new RetryWithRecovery<>("name", getRetryConfig(), supplier, (t) -> true,
        Collections.singletonMap(e -> true, consumer));

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

    RetryWithRecovery<String> r = new RetryWithRecovery<>("name", getRetryConfig(), supplier, (t) -> true,
        Collections.singletonMap(e -> e.isClientError(), consumer));

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

    RetryWithRecovery<String> r = new RetryWithRecovery<>("name", getRetryConfig(), supplier,
        (t) -> t instanceof ApiException, Collections.singletonMap(ApiException::isClientError, consumer));

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

    RetryWithRecovery<String> r = new RetryWithRecovery<>("name", getRetryConfig(), supplier,
        (t) -> true, Collections.singletonMap(ApiException::isClientError, consumer));

    assertEquals(value, r.execute());

    InOrder inOrder = inOrder(supplier, consumer);
    inOrder.verify(supplier).get();
    inOrder.verify(consumer).consume();
    inOrder.verify(supplier).get();
    verifyNoMoreInteractions(supplier, consumer);
  }
}
