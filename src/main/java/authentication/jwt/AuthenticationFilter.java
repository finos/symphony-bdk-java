package authentication.jwt;

import authentication.SymExtensionAppRSAAuth;
import configuration.SymConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationFilter implements Filter {

  private static final String FILTER_INIT = "Starting authentication filter";
  private static final String FILTER_DESTROY = "Destroying authentication filter";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String AUTHORIZATION_HEADER_BEARER_PREFIX = "BEARER";
  private static final String USER_INFO_PROPERTY = "user_info";
  private static final String MISSING_JWT_MESSAGE = "Missing JWT";
  private static final String UNAUTHORIZED_JWT_MESSAGE = "Unauthorized JWT";
  private static final String INTERNAL_SERVER_ERROR_MESSAGE =
    "Unexpected error, please contact the system administrator";
  private static final String USER_ID_FIELDNAME = "sub";
  private static final String ANY_STRING_PATTERN = ".*";

  private SymExtensionAppRSAAuth rsaAuth;
  private SymConfig symConfig;
  private JwtParser parser = Jwts.parser();

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

  public AuthenticationFilter(SymExtensionAppRSAAuth rsaAuth, SymConfig symConfig) {
    this.rsaAuth = rsaAuth;
    this.symConfig = symConfig;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    LoggerFactory.getLogger(AuthenticationFilter.class).info(FILTER_INIT);
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
    FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    if (matchesFilteredPattern(request.getServletPath())) {
      String jwt =
        request.getHeader(AUTHORIZATION_HEADER)
          .substring(AUTHORIZATION_HEADER_BEARER_PREFIX.length());
      if (jwt == null) {
        response.setStatus(401);
        response.getWriter().write(MISSING_JWT_MESSAGE);
        return;
      }

      Jws<Claims> jws;
      try {
        PublicKey rsaVerifier = rsaAuth.getPodPublicKey();
        jws = parser.setSigningKey(rsaVerifier).parseClaimsJws(jwt);
      } catch (CertificateException e) {
        response.setStatus(401);
        response.getWriter().write(UNAUTHORIZED_JWT_MESSAGE);
        return;
      }

      try {
        JwtPayload jwtPayload = new JwtPayload();
        jwtPayload.setUserId(String.valueOf(jws.getBody().get(USER_ID_FIELDNAME)));
        request.setAttribute(USER_INFO_PROPERTY, jwtPayload);

        filterChain.doFilter(request, servletResponse);
        return;
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
        response.setStatus(500);
        response.getWriter().write(INTERNAL_SERVER_ERROR_MESSAGE);
        return;
      }
    }
    filterChain.doFilter(request, servletResponse);
  }

  @Override
  public void destroy() {
    LoggerFactory.getLogger(AuthenticationFilter.class).info(FILTER_DESTROY);
  }

  private Boolean matchesFilteredPattern(String servletPath) {
    return Pattern.compile(
      ANY_STRING_PATTERN + symConfig.getAuthenticationFilterUrlPattern() + ANY_STRING_PATTERN)
      .matcher(servletPath)
      .matches();
  }
}
