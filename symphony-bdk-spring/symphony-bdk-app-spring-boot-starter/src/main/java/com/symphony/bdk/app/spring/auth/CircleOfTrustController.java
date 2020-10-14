package com.symphony.bdk.app.spring.auth;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.model.AppInfo;
import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.auth.model.JwtInfo;
import com.symphony.bdk.app.spring.auth.model.TokenPair;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.exception.AppAuthException;
import com.symphony.bdk.app.spring.exception.InvalidJwtException;
import com.symphony.bdk.app.spring.exception.InvalidTokenException;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.UserClaim;

import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/bdk/v1/app")
public class CircleOfTrustController {

  private final SymphonyBdkAppProperties properties;
  private final ExtensionAppAuthenticator extensionAppAuthenticator;

  public CircleOfTrustController(SymphonyBdkAppProperties properties, ExtensionAppAuthenticator extensionAppAuthenticator) {
    this.properties = properties;
    this.extensionAppAuthenticator = extensionAppAuthenticator;
  }

  @PostMapping("/auth")
  public AppToken authenticate(@Valid @RequestBody AppInfo appInfo) {
    log.debug("App auth step 1: Initializing extension app authentication");

    try {
      AppAuthSession authSession = extensionAppAuthenticator.authenticateExtensionApp(appInfo.getAppToken());
      AppToken appToken = new AppToken();
      appToken.setAppToken(authSession.getAppToken());

      return appToken;
    } catch (AuthUnauthorizedException e) {
      throw new AppAuthException("Failed to authenticate the extension app", e);
    }
  }

  @PostMapping("/tokens")
  public ResponseEntity<?> validateTokens(@Valid @RequestBody TokenPair appToken) {
    log.debug("App auth step 2: Validating tokens");
    if (extensionAppAuthenticator.validateTokens(appToken.getAppToken(), appToken.getSymphonyToken())) {
      return ResponseEntity.noContent().build();
    }

    throw new InvalidTokenException("Failed to validate the app token");
  }

  @PostMapping("/jwt")
  public UserId validateJwt(@Valid @RequestBody JwtInfo jwtInfo, HttpServletRequest request,
      HttpServletResponse response) {
    log.debug("App auth step 3: Validate jwt");
    try {
      String jwt = jwtInfo.getJwt();
      UserClaim userClaim = extensionAppAuthenticator.validateJwt(jwtInfo.getJwt());
      Long userId = userClaim.getId();
      if (properties.getAuth().getJwtCookie().getEnabled()) {
        response.addCookie(jwtCookie(jwt, request.getContextPath()));
      }
      UserId id = new UserId();
      id.setUserId(userId);
      return id;
    } catch (AuthInitializationException e) {
      throw new InvalidJwtException("Failed to validate the jwt", e);
    }
  }

  private Cookie jwtCookie(String jwt, String path) {
    Cookie jwtCookie = new Cookie("userJwt", jwt);

    jwtCookie.setMaxAge(properties.getAuth().getJwtCookie().getMaxAge());
    jwtCookie.setSecure(true);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath(path);

    return jwtCookie;
  }

}
