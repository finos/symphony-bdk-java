package com.symphony.bdk.bot.sdk.webapi.security.config;

import java.util.Arrays;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.symphony.bdk.bot.sdk.symphony.ConfigClient;
import com.symphony.bdk.bot.sdk.webapi.security.JwtCookieFilter;
import com.symphony.bdk.bot.sdk.webapi.security.RequestOriginFilter;
import com.symphony.bdk.bot.sdk.webapi.security.XSSFilter;

/**
 * Security filters configuration
 *
 * @author Marcus Secato
 *
 */
@Configuration
public class SecurityConfig {

  @Bean
  @ConditionalOnProperty(prefix = "cors", name = "allowed-origin")
  public FilterRegistrationBean<CorsFilter> corsFilter(Environment env) {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin(env.getProperty("cors.allowed-origin"));
    config.addAllowedHeader("*");
    config.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(
        env.getProperty("cors.url-mapping"), config);

    FilterRegistrationBean<CorsFilter> bean =
        new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(Integer.MIN_VALUE);

    return bean;
  }

  @Bean
  @ConditionalOnProperty(prefix = "xss", name = "url-mapping")
  public FilterRegistrationBean<XSSFilter> xssFilter(Environment env) {
    FilterRegistrationBean<XSSFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new XSSFilter());
    registrationBean.addUrlPatterns(env.getProperty("xss.url-mapping"));

    return registrationBean;
  }

  @Bean
  @ConditionalOnProperty(prefix = "request-origin", name = "origin-header")
  public FilterRegistrationBean<RequestOriginFilter> notificationOriginFilter(Environment env) {
    FilterRegistrationBean<RequestOriginFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new RequestOriginFilter(
        env.getProperty("request-origin.origin-header")));
    registrationBean.addUrlPatterns(
        env.getProperty("request-origin.url-mapping"));

    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<JwtCookieFilter> jwtCookieFilter(ConfigClient configClient) {
    FilterRegistrationBean<JwtCookieFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new JwtCookieFilter());
    registrationBean.addUrlPatterns(configClient.getExtAppAuthPath() + "*");
    registrationBean.setOrder(1);

    return registrationBean;
  }

}
