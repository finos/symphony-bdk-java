package com.symphony.ms.songwriter.internal.feature;

import org.slf4j.MDC;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private String linkRoomBaseUrl;

  public boolean isCommandFeedbackEnabled() {
    return ENABLED.equals(commandFeedback);
  }

  public boolean isTransactionIdOnErrorEnabled() {
    return ENABLED.equals(transactionIdOnError);
  }

  public String getEventUnexpectedErrorMessage() {
    return eventUnexpectedErrorMessage;
  }

  public void setEventUnexpectedErrorMessage(String eventUnexpectedErrorMessage) {
    this.eventUnexpectedErrorMessage = eventUnexpectedErrorMessage;
  }

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

  public String getLinkRoomBaseUrl() {
    return linkRoomBaseUrl;
  }
}
