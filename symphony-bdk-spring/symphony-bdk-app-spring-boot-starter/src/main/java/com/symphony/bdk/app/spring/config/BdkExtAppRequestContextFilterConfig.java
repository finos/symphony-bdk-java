package com.symphony.bdk.app.spring.config;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.filter.BdkRequestContextFilter;
import com.symphony.bdk.app.spring.filter.TracingFilter;
import com.symphony.bdk.spring.model.TenantRequestContext;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.Ordered;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.RequestContextFilter;

@Slf4j
@ConditionalOnProperty(prefix="bdk", name = "multi-tenant", havingValue = "true")
public class BdkExtAppRequestContextFilterConfig {

  /**
   * This allows request and session scoped beans to work.
   */
  @Bean
  public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
    FilterRegistrationBean<RequestContextFilter> registrationBean = new FilterRegistrationBean<>();
    RequestContextFilter filter = new RequestContextFilter();
    registrationBean.setFilter(filter);
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(1);

    return registrationBean;
  }

  /**
   * Request scope bean for carrying tenant information.
   */
  @Bean
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public TenantRequestContext tenantRequestContext() {
    return new TenantRequestContext();
  }

  @Bean
  public FilterRegistrationBean<BdkRequestContextFilter> tracingFilter(TenantRequestContext tenantRequestContext) {
    final FilterRegistrationBean<BdkRequestContextFilter> registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(new BdkRequestContextFilter(tenantRequestContext));
    // TODO : determine it
    //registrationBean.addUrlPatterns(getUrlPatterns(properties));
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

    return registrationBean;
  }


}
