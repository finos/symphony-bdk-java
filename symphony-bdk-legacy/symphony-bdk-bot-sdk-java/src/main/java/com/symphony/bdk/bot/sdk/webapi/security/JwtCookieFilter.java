package com.symphony.bdk.bot.sdk.webapi.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Filter to pull JWT data from request cookies adding it as authorization
 * request header. Used in SSE authentication as Javascript EventSource does
 * not allow specifying headers.
 *
 * @author Marcus Secato
 *
 */
@ConditionalOnProperty(prefix = "jwt-cookie", name = "enable", havingValue = "true", matchIfMissing = true)
public class JwtCookieFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtCookieFilter.class);
  public static final String JWT_COOKIE_NAME = "userJwt";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String AUTHORIZATION_HEADER_BEARER = "Bearer";

  @Override
  public void init(FilterConfig filterConfig) {
    LOGGER.info("Initializing JwtCookieFilter Filter");
  }

  @Override
  public void doFilter(ServletRequest servletRequest,
      ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String jwt = getJwtFromCookie(request.getCookies());
    if (jwt != null) {
      LOGGER.debug("Pulling JWT from cookie");
      HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
      requestWrapper.addHeader(AUTHORIZATION_HEADER,
          AUTHORIZATION_HEADER_BEARER + " " + jwt);

      chain.doFilter(requestWrapper, response);
    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {
    LOGGER.info("Destroying JwtCookieFilter Filter");
  }

  private String getJwtFromCookie(Cookie[] cookies) {
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (JWT_COOKIE_NAME.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  /**
   * Request wrapper to allow adding new headers
   */
  class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
    private Map<String, String> headerMap = new HashMap<String, String>();

    public HeaderMapRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    public void addHeader(String name, String value) {
      headerMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
      String headerValue = super.getHeader(name);
      if (headerMap.containsKey(name)) {
        headerValue = headerMap.get(name);
      }
      return headerValue;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
      List<String> names = Collections.list(super.getHeaderNames());
      for (String name : headerMap.keySet()) {
        names.add(name);
      }
      return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      List<String> values = Collections.list(super.getHeaders(name));
      if (headerMap.containsKey(name)) {
        values.add(headerMap.get(name));
      }
      return Collections.enumeration(values);
    }
  }
}
