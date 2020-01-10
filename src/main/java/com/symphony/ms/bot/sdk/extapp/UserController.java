package com.symphony.ms.bot.sdk.extapp;

import com.symphony.ms.bot.sdk.internal.symphony.SymphonyService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.NoContentException;

/**
 * Sample code. Implementation of an extension app endpoint for user
 *
 * @author Gabriel Berberian
 */
@RestController
@RequestMapping("/secure/user")
public class UserController {

  private final SymphonyService symphonyService;

  public UserController(SymphonyService symphonyService) {
    this.symphonyService = symphonyService;
  }

  /**
   * Gets an user by username or userId. "local" needs to be filled only when getting by userId
   *
   * @param username
   * @param userId
   * @param local
   * @return the user
   * @throws NoContentException
   */
  @GetMapping
  public ResponseEntity getUserFromId(@RequestParam(required = false) String username,
      @RequestParam(required = false) Long userId, @RequestParam(required = false) Boolean local)
      throws NoContentException {
    if (username != null && !username.isEmpty()) {
      return ResponseEntity.ok(symphonyService.getUserFromUsername(username));
    } else if (userId != null && local != null) {
      return ResponseEntity.ok(symphonyService.getUserFromId(userId, local));
    }
    return ResponseEntity.badRequest()
        .header("Content-Type", "application/json")
        .body("{\"message\": \"missing parameters\"}");
  }

}
