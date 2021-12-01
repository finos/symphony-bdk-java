package com.symphony.bdk.core.retry;


import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiRuntimeException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.function.Predicate;

import javax.net.ssl.SSLHandshakeException;

/**
 * Abstract class to implement a retry mechanism with recovery strategies,
 * e.g. refresh a session in a case of session expiration.
 *
 * @param <T> the type of the object to be eventually returned by the {@link #supplier}
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public abstract class RetryWithRecovery<T> {

  private final SupplierWithApiException<T> supplier;
  private final Predicate<Exception> ignoreException;
  private final List<RecoveryStrategy> recoveryStrategies;
  private final String address;

  /**
   * This is a helper function designed to cover most of the retry cases.
   * It retries on the conditions defined by {@link RetryWithRecoveryBuilder#isNetworkIssueOrMinorError}
   * and refreshes the authSession if we get an unauthorized error.
   *
   * @param baseRetryBuilder the {@link RetryWithRecoveryBuilder} containing the base settings for the retry mechanism.
   * @param name             the name of the retry, can be any string but should specific to the function being retried.
   * @param supplier         the supplier returning the desired object which may fail with an exception.
   * @param <T>              the type of the object to be returned by the supplier.
   * @return the object returned by the supplier
   * @throws ApiRuntimeException if a non-handled {@link ApiException} thrown or if the max number of retries has been reached.
   * @throws RuntimeException    if any other exception thrown.
   */
  public static <T> T executeAndRetry(
      final RetryWithRecoveryBuilder<?> baseRetryBuilder,
      final String name,
      final String address,
      final SupplierWithApiException<T> supplier
  ) {

    final RetryWithRecovery<T> retry = RetryWithRecoveryBuilder.<T>from(baseRetryBuilder)
        .name(name)
        .supplier(supplier)
        .basePath(address)
        .build();

    try {
      return retry.execute();
    } catch (ApiException e) {
      throw new ApiRuntimeException(e);
    } catch (Throwable t) {
      throw new RuntimeException(networkIssueMessageError(t, address), t);
    }
  }

  public RetryWithRecovery(
      SupplierWithApiException<T> supplier,
      Predicate<Exception> ignoreException,
      List<RecoveryStrategy> recoveryStrategies,
      String address
  ) {
    this.supplier = supplier;
    this.ignoreException = ignoreException;
    this.recoveryStrategies = recoveryStrategies;
    this.address = address;
  }

  /**
   * Method called by client which should implement the retry.
   * This should call {@link #executeOnce()} which executes one actual call to the supplier, runs potential recovery
   * actions and potentially throws an exception.
   *
   * @return the object returned by the supplier.
   * @throws Throwable in case the max number of retries exhausted
   *                   or if any other exception thrown by the supplier or the recovery functions.
   */
  public abstract T execute() throws Throwable;

  /**
   * This implements the logic corresponding to one retry:
   * calls the {@link #supplier}, catches the potential {@link Exception},
   * return null if it satisfies {@link #ignoreException}
   * and runs the recovery functions if it matches its corresponding condition.
   * This should be called by any implementation of {@link #execute()}.
   *
   * @return the object returned by the {@link #supplier}.
   * @throws Throwable in case an exception has been thrown by the {@link #supplier} or by the recovery functions.
   */
  protected T executeOnce() throws Throwable {
    try {
      return supplier.get();
    } catch (Exception e) {
      if (ignoreException.test(e)) {
        log.debug("{} ignored: {}", e.getClass().getCanonicalName(), e.getMessage());
        return null;
      }

      handleRecovery(e);
      throw e;
    }
  }

  /**
   * This methods check the type of exception thrown in the retry and depending on that it created a clear
   * error message suggesting the possible cause of the issue.
   * @param t exception found
   * @param address that the retry is trying to reach
   * @return error message
   */
  public static String networkIssueMessageError(Throwable t, String address) {
    String messageError = String.format("An unknown error occurred while trying to connect to %s. Please check below "
        + "for more information: ", address);
    String service = ApiClientFactory.getServiceNameFromBasePath(address).toString();
    if (t.getCause() instanceof SSLHandshakeException) {
      messageError = String.format(
          "Network error occurred while trying to connect to the \"%s\" at the following address: %s. "
              + "Error while trying to validate certificate for the trust store. This type of error typically means "
              + "that your network is using a self-signed certificate.", service, address);
    } else if (t.getCause() instanceof UnknownHostException) {
      messageError = String.format(
          "Network error occurred while trying to connect to the \"%s\" at the following address: %s. Your host is unknown, "
              + "please check that the address is correct. Also consider checking your proxy/firewall connections.",
          service, address);
    } else if (t.getCause() instanceof SocketTimeoutException) {
      messageError = String.format(
          "Timeout occurred while trying to connect to the \"%s\" at the following address: %s. "
              + "Please check that the address is correct. Also consider checking your proxy/firewall connections.",
          service, address);
    } else if (t.getCause() instanceof ConnectException) {
      messageError = String.format(
          "Connection refused while trying to connect to the \"%s\" at the following address: %s. "
              + "Please check if this remote address/port is reachable. Also consider checking your proxy/firewall connections.",
          service, address);
    }
    return messageError;
  }

  private void handleRecovery(Exception e) throws Throwable {
    boolean recoveryTriggered = false;

    for (RecoveryStrategy recoveryStrategy : recoveryStrategies) {
      if (recoveryStrategy.matches(e)) {
        log.debug("Exception recovered", e);
        recoveryTriggered = true;
        recoveryStrategy.runRecovery();
      }
    }

    if (!recoveryTriggered) {
      log.error(networkIssueMessageError(e, address) + e.getMessage());
    }
  }
}
