package com.symphony.bdk.app.spring.auth;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.auth.model.BdkAppErrorCode;
import com.symphony.bdk.app.spring.auth.model.JwtInfo;
import com.symphony.bdk.app.spring.auth.model.TokenPair;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.auth.service.AppTokenService;
import com.symphony.bdk.app.spring.auth.service.JwtService;
import com.symphony.bdk.app.spring.auth.model.exception.AppAuthException;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Rest controller defining Apis for Extension App Authentication's Circle Of Trust.
 */
@Slf4j
@RestController
@RequestMapping("/bdk/v1/app")
public class CircleOfTrustController {

  private final SymphonyBdkAppProperties properties;
  private final ExtensionAppAuthenticator extensionAppAuthenticator;
  private final AppTokenService appTokenService;
  private final JwtService jwtService;

  public CircleOfTrustController(SymphonyBdkAppProperties properties,
      ExtensionAppAuthenticator extensionAppAuthenticator, JwtService jwtService, AppTokenService appTokenService) {
    this.properties = properties;
    this.extensionAppAuthenticator = extensionAppAuthenticator;
    this.appTokenService = appTokenService;
    this.jwtService = jwtService;
  }

  @PostMapping("/auth")
  public AppToken authenticate() {
    log.debug("App auth step 1: Initializing extension app authentication");

    try {
      String token = appTokenService.generateToken();
      AppAuthSession authSession = extensionAppAuthenticator.authenticateExtensionApp(token);
      AppToken appToken = new AppToken();
      appToken.setAppToken(authSession.getAppToken());

      return appToken;
    } catch (AuthUnauthorizedException e) {
      throw new AppAuthException(e, BdkAppErrorCode.UNAUTHORIZED);
    }
  }

  @PostMapping("/tokens")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void validateTokens(@Valid @RequestBody TokenPair appToken) {
    log.debug("App auth step 2: Validating tokens");
    if (!appTokenService.validateTokens(appToken)) {
      throw new AppAuthException(BdkAppErrorCode.INVALID_TOKEN);
    }
  }

  @PostMapping("/jwt")
  public UserId validateJwt(@Valid @RequestBody JwtInfo jwtInfo, HttpServletRequest request,
      HttpServletResponse response) {
    log.debug("App auth step 3: Validate jwt");
    String jwt = jwtInfo.getJwt();
    UserId userId = jwtService.validateJwt(jwt);
    if (properties.getAuth().getJwtCookie().getEnabled()) {
      response.addCookie(jwtCookie(jwt, request.getContextPath()));
    }
    return userId;
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
