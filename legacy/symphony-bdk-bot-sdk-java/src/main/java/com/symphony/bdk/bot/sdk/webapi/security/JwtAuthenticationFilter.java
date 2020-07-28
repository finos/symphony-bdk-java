package com.symphony.bdk.bot.sdk.webapi.security;

import java.io.IOException;

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
import org.slf4j.MDC;

import com.symphony.bdk.bot.sdk.symphony.authentication.ExtensionAppAuthenticator;

import model.UserInfo;

/**
 * Allows/denies access based on the presence of valid JWT token in incoming
 * requests
 *
 * @author Marcus Secato
 *
 */
public class JwtAuthenticationFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer";
  private static final String AUTHORIZATION_TOKENS_DELIMITER = " ";
  private static final String USER_ID = "userId";

  private final ExtensionAppAuthenticator extensionAppAuthenticator;

  public JwtAuthenticationFilter(ExtensionAppAuthenticator extensionAppAuthenticator) {
    this.extensionAppAuthenticator = extensionAppAuthenticator;
  }

  @Override
  public void init(FilterConfig filterConfig) {
    LOGGER.info("Initializing JwtAuthenticationFilter Filter");
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String jwt = extractJwt(request);
    if (jwt != null) {
      UserInfo userInfo = null;
      try {
        userInfo = extensionAppAuthenticator.verifyJWT(jwt);
      } catch (Exception e) {
        LOGGER.info("Unexpected error verifying user JWT", e);
        response.setStatus(500);
        return;
      }

      if (userInfo != null) {
        request.setAttribute(USER_ID, String.valueOf(userInfo.getId()));
        MDC.put(USER_ID, String.valueOf(userInfo.getId()));
        filterChain.doFilter(request, response);
        return;
      }
    }

    LOGGER.debug("Unauthenticated access");
    response.setStatus(401);
  }

  @Override
  public void destroy() {
    LOGGER.info("Destroying JwtAuthenticationFilter Filter");
  }

  private String extractJwt(HttpServletRequest request) {
    String jwt = null;
    String authHeader = request.getHeader(AUTHORIZATION_HEADER);
    if (authHeader != null) {
      String[] authTokens = authHeader.split(AUTHORIZATION_TOKENS_DELIMITER);
      if ((authTokens.length > 1)
          && (authTokens[0].equalsIgnoreCase(AUTHORIZATION_HEADER_PREFIX))) {
        jwt = authTokens[1];
      }
    }

    return jwt;
  }
}
