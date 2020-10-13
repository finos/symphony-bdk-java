package com.symphony.bdk.app.spring.controller;

import com.symphony.bdk.app.spring.model.AppInfo;
import com.symphony.bdk.app.spring.model.AppToken;
import com.symphony.bdk.app.spring.model.JwtInfo;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.UserClaim;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping(value = {"/application", "/applications"})
@ConditionalOnProperty(name = "bdk.app.auth.enabled", havingValue = "true")
public class AppAuthController {

  @Value("${jwt-cookie.enable:false}")
  private Boolean jwtCookieEnable;

  private final ExtensionAppAuthenticator extensionAppAuthenticator;
  private final SymphonyBdkCoreProperties properties;

  public AppAuthController(SymphonyBdkCoreProperties properties, ExtensionAppAuthenticator extensionAppAuthenticator) {
    this.properties = properties;
    this.extensionAppAuthenticator = extensionAppAuthenticator;
  }

  @PostMapping("authenticate")
  public ResponseEntity<AppAuthSession> authenticate(@RequestBody AppInfo appInfo) {
    log.debug("App auth step 1: Initializing extension app authentication");

    if (appInfo.getAppId() == null) {
      log.debug("Rejecting authenticate request: null app ID received");
      return ResponseEntity.badRequest().build();
    }

    if (appInfo.getAppToken() == null) {
      log.debug("Rejecting authenticate request: null app token received");
      return ResponseEntity.badRequest().build();
    }

    if (!appInfo.getAppId().equals(properties.getApp().getAppId())) {
      log.debug("Rejecting authenticate request: app ID does not match");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    try {
      AppAuthSession authSession = extensionAppAuthenticator.authenticateExtensionApp(appInfo.getAppToken());
      return ResponseEntity.ok(authSession);
    } catch (AuthUnauthorizedException authUnauthorizedException) {
      log.info("Authentication process failed");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("tokens/validate")
  public ResponseEntity<?> validateTokens(@RequestBody AppToken appToken) {
    log.debug("App auth step 2: Validating tokens");
    if (extensionAppAuthenticator.validateTokens(appToken.getAppToken(), appToken.getSymphonyToken())) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @PostMapping("jwt/validate")
  public ResponseEntity<Long> validateJwt(@RequestBody JwtInfo jwtInfo, HttpServletRequest request,
      HttpServletResponse response) {
    log.debug("App auth step 3: Validate jwt");
    try {
      String jwt = jwtInfo.getJwt();
      UserClaim userClaim = extensionAppAuthenticator.validateJwt(jwtInfo.getJwt());
      Long userId = userClaim.getId();
      if (jwtCookieEnable) {
        response.addCookie(jwtCookie(jwt, request.getContextPath()));
      }
      return ResponseEntity.ok(userId);
    } catch (AuthInitializationException e) {
      log.info("Authentication process failed");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  private Cookie jwtCookie(String jwt, String path) {
    Cookie jwtCookie = new Cookie("userJwt", jwt);

    // 1 day
    jwtCookie.setMaxAge(24 * 60 * 60);
    jwtCookie.setSecure(true);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath(path);

    return jwtCookie;
  }

}
