package com.symphony.bdk.app.spring.auth;

import com.symphony.bdk.app.spring.SymphonyBdkAppProperties;
import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.auth.model.JwtInfo;
import com.symphony.bdk.app.spring.auth.model.TokenPair;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.auth.service.CircleOfTrustService;

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
  private final CircleOfTrustService circleOfTrustService;

  public CircleOfTrustController(SymphonyBdkAppProperties properties, CircleOfTrustService circleOfTrustService) {
    this.properties = properties;
    this.circleOfTrustService = circleOfTrustService;
  }

  @PostMapping("/auth")
  public AppToken authenticate() {
    log.debug("Generate app token and use it to authenticate the extension app.");

    return circleOfTrustService.authenticate();
  }

  @PostMapping("/tokens")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void validateTokens(@Valid @RequestBody TokenPair tokenPair) {
    log.debug("Validate the pair of tokens: app token and Symphony token.");
    circleOfTrustService.validateTokens(tokenPair);
  }

  @PostMapping("/jwt")
  public UserId validateJwt(@Valid @RequestBody JwtInfo jwtInfo, HttpServletRequest request,
      HttpServletResponse response) {
    log.debug("Validate the jwt signed by extension app frontend to get the user id");
    String jwt = jwtInfo.getJwt();
    log.debug("JWT " + jwt);
    UserId userId = circleOfTrustService.validateJwt(jwt);
    log.debug("USERID " + userId.getUserId());
    if (properties.getAuth().getJwtCookie().getEnabled()) {
      response.addCookie(jwtCookie(jwt, request.getContextPath()));
    }
    return userId;
  }

  private Cookie jwtCookie(String jwt, String path) {
    Cookie jwtCookie = new Cookie("userJwt", jwt);

    jwtCookie.setMaxAge((int) properties.getAuth().getJwtCookie().getMaxAge().getSeconds());
    jwtCookie.setSecure(true);
    jwtCookie.setHttpOnly(true);
    jwtCookie.setPath(path);

    return jwtCookie;
  }

}
