package com.symphony.bdk.bot.sdk.webapi.security;

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

/**
 * Cross-site scripting filter
 *
 * @author Marcus Secato
 *
 */
public class XSSFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(XSSFilter.class);

  @Override
  public void init(FilterConfig filterConfig) {
    LOGGER.info("XSSFilter has been registered");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
  }

  @Override
  public void destroy() {
    LOGGER.info("XSSFilter has been destroyed");
  }

}
