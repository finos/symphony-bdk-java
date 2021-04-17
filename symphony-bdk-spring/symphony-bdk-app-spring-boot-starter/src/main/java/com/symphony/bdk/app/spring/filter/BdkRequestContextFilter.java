package com.symphony.bdk.app.spring.filter;

import com.symphony.bdk.app.spring.exception.BdkAppErrorCode;
import com.symphony.bdk.app.spring.exception.BdkAppException;
import com.symphony.bdk.http.api.tracing.DistributedTracingContext;

import com.symphony.bdk.spring.model.TenantRequestContext;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Tracing filter based on {@link DistributedTracingContext} defined in symphony-bdk-http-api module.
 */
@Slf4j
@RequiredArgsConstructor
public class BdkRequestContextFilter extends OncePerRequestFilter {

  private final TenantRequestContext tenantRequestContext;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    try {
      tenantRequestContext.setTenantId(request.getHeader("ext-tenantId"));
      tenantRequestContext.setTenantHost(request.getHeader("X-Symphony-Host"));
    } catch(Exception e) {
      throw new BdkAppException(BdkAppErrorCode.MISSING_TENANT_HEADERS);
    }
    filterChain.doFilter(request, response);
  }
}
