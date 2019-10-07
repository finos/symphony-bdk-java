package com.symphony.ms.songwriter.internal.extapp.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.symphony.ms.songwriter.internal.symphony.AppAuthenticateException;
import com.symphony.ms.songwriter.internal.symphony.SymphonyService;
import com.symphony.ms.songwriter.internal.symphony.model.AuthenticateResponse;

@RestController
@RequestMapping(value = "/application")
public class AppAuthController {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppAuthController.class);

  private SymphonyService symphonyService;

  public AppAuthController(SymphonyService symphonyService) {
    this.symphonyService = symphonyService;
  }

  @PostMapping("authenticate")
  public ResponseEntity authenticate(@RequestBody AppInfo appInfo) {
    LOGGER.debug("App auth step 1: Initializing extension app authentication");
    if (appInfo.getAppId() == null) {
      return ResponseEntity.badRequest().build();
    }

    try {
      AuthenticateResponse authenticateResponse =
          symphonyService.appAuthenticate(appInfo.getAppId());
      return ResponseEntity.ok(authenticateResponse);
    } catch (AppAuthenticateException aae) {
      LOGGER.error("Error initializing extension app authentication flow");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("tokens/validate")
  public ResponseEntity validateTokens(@RequestBody AppToken appToken) {
    LOGGER.debug("App auth step 2: Validating tokens");
    if (symphonyService.validateTokens(
        appToken.getAppToken(), appToken.getSymphonyToken())) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @PostMapping("jwt/validate")
  public ResponseEntity validateJwt(@RequestBody JwtInfo jwtInfo) {
    LOGGER.debug("App auth step 3: Validating JWT");
    try {
      return ResponseEntity.ok(
          symphonyService.verifyJWT(jwtInfo.getJwt()));
    } catch (AppAuthenticateException aae) {
      LOGGER.error("Error validating JWT");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

}
