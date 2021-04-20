package com.symphony.bdk.http.jersey2;

import static org.slf4j.LoggerFactory.getLogger;

import org.apiguardian.api.API;
import org.slf4j.Logger;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * This custom request/response filter allows to output the total request time under log 'com.symphony.bdk.requests.outgoing'.
 * This will only be available is logging level is set to 'DEBUG' in your implementation logger configuration.
 */
@Provider
@API(status = API.Status.INTERNAL)
public class ApiClientJersey2RequestLogFilter implements ClientRequestFilter, ClientResponseFilter {

  private static final Logger log = getLogger("com.symphony.bdk.requests.outgoing");

  private static final String INTERNAL_CLIENT_REQUEST_START_TIME = "X-BDK-internal-request-start-time";
  private static final String INTERNAL_REQUEST_LOG_MESSAGE = "status={}, url={}, time={}";

  /**
   * Set the current time in headers when request is sent.
   */
  @Override
  public void filter(ClientRequestContext requestContext) {
    if (log.isDebugEnabled()) {
      requestContext.setProperty(INTERNAL_CLIENT_REQUEST_START_TIME, System.currentTimeMillis());
    }
  }

  /**
   * Compute request execution time using header value previously set right above.
   */
  @Override
  public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
    if (log.isDebugEnabled()) {
      final Long startTimeRequest = (Long) requestContext.getProperty(INTERNAL_CLIENT_REQUEST_START_TIME);
      // might never happen we never know, custom headers could be dropped by customer infra
      if (startTimeRequest != null) {
        final long totalRequestTime = System.currentTimeMillis() - startTimeRequest;
        log.debug(INTERNAL_REQUEST_LOG_MESSAGE, responseContext.getStatus(), requestContext.getUri(), totalRequestTime);
      }
    }
  }
}
