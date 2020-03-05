package com.symphony.bot.sdk.internal.extapp.authentication;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import authentication.jwt.JwtPayload;

/**
 * Filter to pull user details from JWT payload making them easily accessible
 * for downstream request handlers.
 *
 * @author Marcus Secato
 *
 */
@Component
@Order(2)
public class UserInfoFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoFilter.class);

  private static final String USER_ID = "userId";

  @Override
  public void init(FilterConfig filterConfig) {
    LOGGER.info("Initializing UserInfo Filter");
  }

  @Override
  public void doFilter(ServletRequest request,
      ServletResponse response, FilterChain filterChain)
    throws IOException, ServletException {

    if (request.getAttribute("user_info") != null) {
      try {
        String userId = ((JwtPayload) request.getAttribute("user_info")).getUserId();
        request.setAttribute(USER_ID, userId);
        MDC.put(USER_ID, userId);
      }
      catch (Exception e){
        LOGGER.error(e.getMessage(), e);
      }
    }
    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    LOGGER.info("Destroying UserInfo Filter");
  }

}
