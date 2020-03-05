package com.symphony.bot.sdk.extapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.symphony.bot.sdk.internal.symphony.UsersClient;
import com.symphony.bot.sdk.internal.symphony.exception.SymphonyClientException;

/**
 * Sample code. Implementation of an extension app endpoint for user
 *
 * @author Gabriel Berberian
 */
@RestController
@RequestMapping("/secure/users")
public class UsersController {

  private final UsersClient usersClient;

  public UsersController(UsersClient usersClient) {
    this.usersClient = usersClient;
  }

  /**
   * Gets an user by username or userId. "local" needs to be filled only when getting by userId
   *
   * @param username
   * @param userId
   * @param local
   * @return the user
   */
  @GetMapping
  public ResponseEntity getUserFromId(@RequestParam(required = false) String username,
      @RequestParam(required = false) Long userId, @RequestParam(required = false) Boolean local) {
    try {
      if (username != null && !username.isEmpty()) {
        return ResponseEntity.ok(usersClient.getUserFromUsername(username));
      } else if (userId != null && local != null) {
        return ResponseEntity.ok(usersClient.getUserFromId(userId, local));
      }
      return ResponseEntity.badRequest()
          .header("Content-Type", "application/json")
          .body("{\"message\": \"missing parameters\"}");
    } catch (SymphonyClientException sce) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}
