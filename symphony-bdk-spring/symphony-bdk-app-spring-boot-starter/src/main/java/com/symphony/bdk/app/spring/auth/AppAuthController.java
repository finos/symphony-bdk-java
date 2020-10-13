package com.symphony.bdk.app.spring.auth;

import com.symphony.bdk.app.spring.auth.model.AppInfo;
import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.auth.model.JwtInfo;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.UserClaim;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/bdk/v1/app/auth")
public class AppAuthController {

  @Value("${bdk.app.auth.jwt-cookie.enabled:false}")
  private Boolean jwtCookieEnable;

  @Value("${bdk.app.auth.jwt-cookie.expire-in:86400}")
  private Integer jwtCookieExpireInSec;

  private final ExtensionAppAuthenticator extensionAppAuthenticator;

  public AppAuthController(ExtensionAppAuthenticator extensionAppAuthenticator) {
    this.extensionAppAuthenticator = extensionAppAuthenticator;
  }

  @PostMapping
  public ResponseEntity<AppAuthSession> authenticate(@Valid @RequestBody AppInfo appInfo)
      throws AuthUnauthorizedException {
    log.debug("App auth step 1: Initializing extension app authentication");

    AppAuthSession authSession = extensionAppAuthenticator.authenticateExtensionApp(appInfo.getAppToken());
    return ResponseEntity.ok(authSession);
  }

  @PostMapping("/tokens")
  public ResponseEntity<?> validateTokens(@Valid @RequestBody AppToken appToken) {
    log.debug("App auth step 2: Validating tokens");
    if (extensionAppAuthenticator.validateTokens(appToken.getAppToken(), appToken.getSymphonyToken())) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @PostMapping("/jwt")
  public ResponseEntity<Long> validateJwt(@Valid @RequestBody JwtInfo jwtInfo, HttpServletRequest request,
      HttpServletResponse response) throws AuthInitializationException {
    log.debug("App auth step 3: Validate jwt");
    String jwt = jwtInfo.getJwt();
    UserClaim userClaim = extensionAppAuthenticator.validateJwt(jwtInfo.getJwt());
    Long userId = userClaim.getId();
    if (jwtCookieEnable) {
      response.addCookie(jwtCookie(jwt, request.getContextPath()));
    }
    return ResponseEntity.ok(userId);
  }

  private Cookie jwtCookie(String jwt, String path) {
    Cookie jwtCookie = new Cookie("userJwt", jwt);

    // 1 day
    jwtCookie.setMaxAge(jwtCookieExpireInSec);
    jwtCookie.setSecure(true);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath(path);

    return jwtCookie;
  }

}
