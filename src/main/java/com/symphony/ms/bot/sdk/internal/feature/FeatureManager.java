package com.symphony.ms.bot.sdk.internal.feature;

import org.slf4j.MDC;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Loads feature flags and details from properties file to control/customize
 * bot's features.
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("features")
public class FeatureManager {

  private static final String ENABLED = "enabled";

  private String commandFeedback;
  private String transactionIdOnError;
  private String eventUnexpectedErrorMessage;
  private String notificationBaseUrl;

  /**
   * Whether bot is allowed to send a response in Symphony chat upon completing
   * handling a command or event.
   *
   * @return true if bot is allowed to send responses, false otherwise
   */
  public boolean isCommandFeedbackEnabled() {
    return ENABLED.equals(commandFeedback);
  }

  /**
   * Whether bot should include the transaction ID from log context when
   * responding to a failed command/event handling. Depends on whether command
   * feedback is enabled.
   *
   * @return true if bot must include transaction ID, false otherwise
   */
  public boolean isTransactionIdOnErrorEnabled() {
    return ENABLED.equals(transactionIdOnError);
  }

  /**
   * A default message to be sent on Symphony chat when unexpected errors occur
   * when handling events or commands. Depends on whether command feedback is
   * enabled.
   *
   * @return the default message for unexpected error
   */
  public String getEventUnexpectedErrorMessage() {
    return eventUnexpectedErrorMessage;
  }

  public void setEventUnexpectedErrorMessage(String eventUnexpectedErrorMessage) {
    this.eventUnexpectedErrorMessage = eventUnexpectedErrorMessage;
  }

  /**
   * Combine features to properly generate a message for unexpected errors
   *
   * @return the message for unexpected error
   */
  public String unexpectedErrorResponse() {
    String errorMessage = null;
    if (isCommandFeedbackEnabled()
        && getEventUnexpectedErrorMessage() != null) {
      errorMessage = getEventUnexpectedErrorMessage();
      if (isTransactionIdOnErrorEnabled()) {
        errorMessage += " (code=" + MDC.get("transactionId") + ")";
      }
    }

    return errorMessage;
  }

  /**
   * Base URL to receive notification from external systems
   *
   * @return base url
   */
  public String getNotificationBaseUrl() {
    return notificationBaseUrl;
  }
}
