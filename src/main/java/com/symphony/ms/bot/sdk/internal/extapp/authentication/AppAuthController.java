package com.symphony.ms.bot.sdk.internal.extapp.authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.symphony.ms.bot.sdk.internal.symphony.ConfigClient;
import com.symphony.ms.bot.sdk.internal.symphony.ExtensionAppAuthClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.AppAuthenticateException;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.AuthenticateResponse;
import com.symphony.ms.bot.sdk.internal.webapi.security.JwtCookieFilter;

/**
 * Extension App authentication controller
 * Exposes endpoints through which an extension app could authenticate itself
 * to Symphony.
 *
 * @author Marcus Secato
 *
 */
@RestController
@RequestMapping(value = "/application")
public class AppAuthController {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppAuthController.class);

  private ExtensionAppAuthClient extAppAuthClient;
  private ConfigClient configClient;

  public AppAuthController(ExtensionAppAuthClient extAppAuthClient, ConfigClient configClient) {
    this.extAppAuthClient = extAppAuthClient;
    this.configClient = configClient;
  }

  @PostMapping("authenticate")
  public ResponseEntity authenticate(@RequestBody AppInfo appInfo) {
    LOGGER.debug("App auth step 1: Initializing extension app authentication");

    if (appInfo.getAppId() == null) {
      LOGGER.debug("Rejecting authenticate request: null app ID received");
      return ResponseEntity.badRequest().build();
    }

    if (!appInfo.getAppId().equals(configClient.getExtAppId())) {
      LOGGER.debug("Rejecting authenticate request: app ID does not match");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    try {
      AuthenticateResponse authenticateResponse =
          extAppAuthClient.appAuthenticate(appInfo.getAppId());
      return ResponseEntity.ok(authenticateResponse);
    } catch (AppAuthenticateException aae) {
      LOGGER.info("Authentication process failed");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (SymphonyClientException sce) {
      LOGGER.error("Error initializing extension app authentication flow\n{}", sce);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("tokens/validate")
  public ResponseEntity validateTokens(@RequestBody AppToken appToken) {
    LOGGER.debug("App auth step 2: Validating tokens");
    if (extAppAuthClient.validateTokens(
        appToken.getAppToken(), appToken.getSymphonyToken())) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @PostMapping("jwt/validate")
  public ResponseEntity validateJwt(@RequestBody JwtInfo jwtInfo, HttpServletResponse response) {
    LOGGER.debug("App auth step 3: Validating JWT");
    try {
      String jwt = jwtInfo.getJwt();
      Long userId = extAppAuthClient.verifyJWT(jwt);
      response.addCookie(jwtCookie(jwt));

      return ResponseEntity.ok(userId);
    } catch (AppAuthenticateException aae) {
      LOGGER.error("Error validating JWT");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  private Cookie jwtCookie(String jwt) {
    Cookie jwtCookie = new Cookie(JwtCookieFilter.JWT_COOKIE_NAME, jwt);

    // 1 day
    jwtCookie.setMaxAge(1 * 24 * 60 * 60);
    jwtCookie.setSecure(true);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath(configClient.getExtAppAuthPath());

    return jwtCookie;
  }

}
