package com.symphony.bdk.core.service.datafeed;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

/**
 * Interface definition for a loop service to be used for handling the datahose API.
 */
@API(status = API.Status.STABLE)
public interface DatahoseLoop extends DatafeedLoop {

  /**
   * Start the datahose events service
   *
   */
  void start() throws AuthUnauthorizedException, ApiException;

  /**
   * Stop the datahose events service.
   * The datahose service will be stopped after a small delay to finish the last read datahose call.
   *
   */
  void stop();

  /**
   * {@inheritDoc}
   */
  void subscribe(RealTimeEventListener listener);

  /**
   * {@inheritDoc}
   */
  void unsubscribe(RealTimeEventListener listener);
}
