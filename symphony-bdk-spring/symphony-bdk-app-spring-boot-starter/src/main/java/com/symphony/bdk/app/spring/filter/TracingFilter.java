package com.symphony.bdk.app.spring.filter;

import com.symphony.bdk.http.api.tracing.DistributedTracingContext;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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
