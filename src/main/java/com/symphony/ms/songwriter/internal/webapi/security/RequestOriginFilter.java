package com.symphony.ms.songwriter.internal.webapi.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestOriginFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestOriginFilter.class);

  private String originHeader;

  public RequestOriginFilter(String originHeader) {
    this.originHeader = originHeader;
  }

  @Override
  public void init(FilterConfig filterConfig) {
    LOGGER.info("RequestOriginFilter has been registered");
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
    String reqOriginHeader = httpRequest.getHeader(originHeader);

    if (reqOriginHeader != null && !reqOriginHeader.isEmpty()) {
      LOGGER.debug("Received request with origin header");
      filterChain.doFilter(servletRequest, servletResponse);
    }
  }

  @Override
  public void destroy() {
    LOGGER.info("RequestOriginFilter has been destroyed");
  }

}
