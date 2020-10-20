package com.symphony.bdk.core.auth.impl;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.junit.jupiter.api.Test;

import javax.ws.rs.ProcessingException;

class AuthenticationRetryTest {

  @Test
  void testCallSucceedsFirstTime() throws AuthUnauthorizedException {
    AuthenticationRetry<String> authenticationRetry = new AuthenticationRetry<>(ofMinimalInterval(1));

    final String output = "output";
    assertEquals(output, authenticationRetry.executeAndRetry("test", () -> output, ""));
  }

  @Test
  void testCallFailsWithServerErrorSucceedsSecondTime() throws ApiException, AuthUnauthorizedException {
    final String output = "output";

    SupplierWithApiException<String> supplier = mock(SupplierWithApiException.class);
    when(supplier.get()).thenThrow(new ApiException(502, "")).thenReturn(output);

    AuthenticationRetry<String> authenticationRetry = new AuthenticationRetry<>(ofMinimalInterval(2));
    assertEquals(output, authenticationRetry.executeAndRetry("test", supplier, ""));

    verify(supplier, times(2)).get();
    verifyNoMoreInteractions(supplier);
  }

  @Test
  void testCallFailsWithTooManyRequestsSucceedsSecondTime() throws AuthUnauthorizedException, ApiException {
    final String output = "output";

    SupplierWithApiException<String> supplier = mock(SupplierWithApiException.class);
    when(supplier.get()).thenThrow(new ApiException(429, "")).thenReturn(output);

    AuthenticationRetry<String> authenticationRetry = new AuthenticationRetry<>(ofMinimalInterval(2));

    assertEquals(output, authenticationRetry.executeAndRetry("test", supplier, ""));
    verify(supplier, times(2)).get();
    verifyNoMoreInteractions(supplier);
  }

  @Test
  void testCallFailsWithProcessingExceptionSucceedsSecondTime() throws AuthUnauthorizedException, ApiException {
    final String output = "output";

    SupplierWithApiException<String> supplier = mock(SupplierWithApiException.class);
    when(supplier.get()).thenThrow(new ProcessingException("")).thenReturn(output);

    AuthenticationRetry<String> authenticationRetry = new AuthenticationRetry<>(ofMinimalInterval(2));

    assertEquals(output, authenticationRetry.executeAndRetry("test", supplier, ""));
    verify(supplier, times(2)).get();
    verifyNoMoreInteractions(supplier);
  }

  @Test
  void testCallFailsAfterRetriesExhausted() throws ApiException {
    SupplierWithApiException<String> supplier = mock(SupplierWithApiException.class);
    when(supplier.get()).thenThrow(new ApiException(429, ""));

    AuthenticationRetry<String> authenticationRetry = new AuthenticationRetry<>(ofMinimalInterval(2));

    assertThrows(ApiRuntimeException.class, () -> authenticationRetry.executeAndRetry("test", supplier, ""));
    verify(supplier, times(2)).get();
    verifyNoMoreInteractions(supplier);
  }

  @Test
  void testCallFailsWithUnauthorized() throws ApiException {
    SupplierWithApiException<String> supplier = mock(SupplierWithApiException.class);
    when(supplier.get()).thenThrow(new ApiException(401, ""));

    AuthenticationRetry<String> authenticationRetry = new AuthenticationRetry<>(ofMinimalInterval(2));

    assertThrows(AuthUnauthorizedException.class, () -> authenticationRetry.executeAndRetry("test", supplier, ""));
    verify(supplier, times(1)).get();
    verifyNoMoreInteractions(supplier);
  }

  @Test
  void testCallFailsWithUnexpectedApiException() throws ApiException {
    SupplierWithApiException<String> supplier = mock(SupplierWithApiException.class);
    when(supplier.get()).thenThrow(new ApiException(404, ""));

    AuthenticationRetry<String> authenticationRetry = new AuthenticationRetry<>(ofMinimalInterval(2));

    assertThrows(ApiRuntimeException.class, () -> authenticationRetry.executeAndRetry("test", supplier, ""));
    verify(supplier, times(1)).get();
    verifyNoMoreInteractions(supplier);
  }

  @Test
  void testCallFailsWithUnexpectedError() throws ApiException {
    SupplierWithApiException<String> supplier = mock(SupplierWithApiException.class);
    when(supplier.get()).thenThrow(new IllegalStateException(""));

    AuthenticationRetry<String> authenticationRetry = new AuthenticationRetry<>(ofMinimalInterval(2));

    assertThrows(RuntimeException.class, () -> authenticationRetry.executeAndRetry("test", supplier, ""));
    verify(supplier, times(1)).get();
    verifyNoMoreInteractions(supplier);
  }

}
