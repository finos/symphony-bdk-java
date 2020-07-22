package com.symphony.bdk.bot.sdk.webapi.throttling;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.util.concurrent.RateLimiter;

/**
 * Limits request rate based on either request origin (IP address) or target endpoint.
 *
 * @author msecato
 *
 */
public class ThrottlingFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingFilter.class);

  private ConcurrentHashMap<String, RateLimiter> limiters;
  private int limit;
  private ThrottlingModeEnum mode;
  private long timeout;

  private static final String[] HEADERS_LIST = {
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
  };

  public ThrottlingFilter(int limit, ThrottlingModeEnum mode, long timeout) {
    this.limit = limit;
    this.mode = mode;
    this.timeout = timeout;
    limiters = new ConcurrentHashMap<String, RateLimiter>();
  }

  @Override
  public void init(FilterConfig filterConfig) {
    LOGGER.info("ThrottlingFilter has been registered");
  }

  @Override
  public void doFilter(ServletRequest servletRequest,
      ServletResponse servletResponse, FilterChain filterChain)
          throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String throttleKey = getThrottleKey(request);

    RateLimiter limiter = limiters.computeIfAbsent(throttleKey, createLimiter());
    if (limiter.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
    }

  }

  @Override
  public void destroy() {
    LOGGER.info("ThrottlingFilter has been destroyed");
  }

  private String getThrottleKey(HttpServletRequest request) {
    if (ThrottlingModeEnum.ORIGIN.equals(mode)) {
      return getIpAddress(request);
    } else {
      return request.getRequestURI();
    }
  }

  private String getIpAddress(HttpServletRequest request) {
    for (String header : HEADERS_LIST) {
      String ip = request.getHeader(header);
      if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
        return ip;
      }
    }
    return request.getRemoteAddr();
  }

  private Function<String, RateLimiter> createLimiter() {
    return name -> RateLimiter.create(limit);
  }

}
