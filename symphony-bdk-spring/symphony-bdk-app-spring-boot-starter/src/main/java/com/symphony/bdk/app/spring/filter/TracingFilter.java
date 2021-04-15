package com.symphony.bdk.app.spring.filter;

import com.symphony.bdk.http.api.tracing.DistributedTracingContext;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Tracing filter based on {@link DistributedTracingContext} defined in symphony-bdk-http-api module.
 */
@Slf4j
public class TracingFilter implements Filter {

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {

    final HttpServletRequest request = (HttpServletRequest) servletRequest;
    final HttpServletResponse response = (HttpServletResponse) servletResponse;

    // Trace ID can be issued from another calling system
    final String xTraceInHeader = request.getHeader(DistributedTracingContext.TRACE_ID);

    if (xTraceInHeader == null || xTraceInHeader.isEmpty()) {
      // init default one
      DistributedTracingContext.setTraceId();
    } else {
      // generate new value appended to existing one
      DistributedTracingContext.setBaseTraceId(xTraceInHeader);
    }

    response.setHeader(DistributedTracingContext.TRACE_ID, DistributedTracingContext.getTraceId());

    try {
      filterChain.doFilter(servletRequest, servletResponse);
    } finally {
      DistributedTracingContext.clear();
    }
  }
}
