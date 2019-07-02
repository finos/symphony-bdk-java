package ${package}.logging;

import authentication.jwt.JwtPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MDCUserInfoFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      ${package}.logging.MDCUserInfoFilter.class);

  @Override
  public void init(FilterConfig filterConfig) {
    LOGGER.info("Initializing MDC Post Filter");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
    throws IOException, ServletException {
    if (request.getAttribute("user_info") != null) {
      try {
        MDC.put("UserId", ((JwtPayload) request.getAttribute("user_info")).getUserId());
      }
      catch (Exception e){
        LOGGER.error(e.getMessage(), e);
      }
    }
    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    LOGGER.info("Destroying MDC Post Filter");
  }
}
