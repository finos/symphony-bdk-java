package com.symphony.ms.bot.sdk.internal.notification;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.symphony.ms.bot.sdk.internal.notification.model.NotificationRequest;
import com.symphony.ms.bot.sdk.internal.symphony.MessageClientImpl;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * Notification controller
 * Exposes an endpoint through which external systems can send messages to the
 * bot application.
 *
 * @author Marcus Secato
 *
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

  private InterceptorChain interceptorChain;

  private MessageClientImpl messageClient;

  public NotificationController(InterceptorChain interceptorChain,
      MessageClientImpl messageClient) {
    this.interceptorChain = interceptorChain;
    this.messageClient = messageClient;
  }

  @PostMapping(value = "/{identifier}")
  public ResponseEntity<String> receiveNotification(
      @RequestBody String notification,
      @RequestHeader Map<String, String> headers,
      @PathVariable(value = "identifier") String identifier) {

    try {
      NotificationRequest notificationRequest = new NotificationRequest(
          headers, notification, identifier);
      final SymphonyMessage notificationMessage = new SymphonyMessage();

      boolean result = interceptorChain.execute(
          notificationRequest, notificationMessage);

      if (result
          && notificationMessage.hasContent()
          && notificationRequest.getStreamId() != null) {
        LOGGER.debug("Sending notification for stream {}",
            notificationRequest.getStreamId());
        messageClient._sendMessage(
            notificationRequest.getStreamId(), notificationMessage);
      }

      return ResponseEntity.ok().build();
    } catch (Exception e) {
      LOGGER.error("Error processing notification request", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

  }

}
