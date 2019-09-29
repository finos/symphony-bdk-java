package com.symphony.ms.songwriter.internal.lib.webapi.logging;

import java.util.UUID;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@WebListener
public class RequestListener implements ServletRequestListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestListener.class);

  private static final String TRANSACTION_ID = "transactionId";

  @Override
  public void requestInitialized(ServletRequestEvent event) {
    MDC.put(TRANSACTION_ID, String.valueOf(UUID.randomUUID()));
    HttpServletRequest httpRequest = (HttpServletRequest) event.getServletRequest();

    String authorization = httpRequest.getHeader("Authorization");
    boolean isBasic = (authorization != null
        && !authorization.isEmpty()
        && authorization.toLowerCase().startsWith("basic"));

    LOGGER.debug("REQUEST INITIALIZED: Remote IP - {}, Request URL - {}, Basic auth - {}",
        httpRequest.getRemoteAddr(), httpRequest.getRequestURL(), isBasic);
  }

  @Override
  public void requestDestroyed(ServletRequestEvent event) {
    MDC.clear();
    LOGGER.debug("REQUEST DESTROYED");
  }

}
