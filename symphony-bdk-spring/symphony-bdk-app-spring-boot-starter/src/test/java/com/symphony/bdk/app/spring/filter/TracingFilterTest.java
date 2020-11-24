package com.symphony.bdk.app.spring.filter;

import static org.assertj.core.api.Assertions.assertThat;

import com.symphony.bdk.http.api.tracing.DistributedTracingContext;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class TracingFilterTest {

  @Test
  void traceIdPresentInRequest() throws Exception {

    final String incomingTraceId = "123456";

    final TracingFilter filter = new TracingFilter();

    final MockHttpServletRequest req = new MockHttpServletRequest();
    req.addHeader(DistributedTracingContext.TRACE_ID, incomingTraceId);

    final MockHttpServletResponse res = new MockHttpServletResponse();
    final MockFilterChain chain = new MockFilterChain();

    filter.doFilter(req, res, chain);

    assertThat(res.getHeader(DistributedTracingContext.TRACE_ID)).startsWith(incomingTraceId + ":");
  }

  @Test
  void traceIdNotPresentInRequest() throws Exception {

    final TracingFilter filter = new TracingFilter();

    final MockHttpServletRequest req = new MockHttpServletRequest();
    final MockHttpServletResponse res = new MockHttpServletResponse();
    final MockFilterChain chain = new MockFilterChain();

    filter.doFilter(req, res, chain);

    assertThat(res.getHeader(DistributedTracingContext.TRACE_ID)).isNotBlank();
  }

}
