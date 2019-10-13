package com.symphony.ms.songwriter.internal.symphony.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.HealthcheckResponse;

@Data
@NoArgsConstructor
public class HealthCheckInfo {
  private static final String CONNECTED = "UP";
  private static final String DISCONNECTED = "DOWN";
  private static final String NOT_AVAILABLE = "N/A";

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private boolean overallStatus;

  private String agentConnection;
  private String podConnection;
  private String agentToPodConnection;
  private String agentToKMConnection;
  private String podVersion;
  private String agentVersion;
  private String agentToPodConnectionError;
  private String agentToKMConnectionError;

  public HealthCheckInfo(
      HealthcheckResponse healthcheckResponse, boolean isPodUp) {

    this.podConnection = convert(isPodUp);
    if (healthcheckResponse != null) {
      this.agentConnection = convert(
          healthcheckResponse.getAgentServiceUser());
      this.agentToPodConnection = convert(
          healthcheckResponse.getPodConnectivity());
      this.agentToKMConnection = convert(
          healthcheckResponse.getKeyManagerConnectivity());
      this.podVersion = healthcheckResponse.getPodVersion();
      this.agentVersion = healthcheckResponse.getAgentVersion();
      this.agentToPodConnectionError = defaultValue(
          healthcheckResponse.getPodConnectivityError());
      this.agentToKMConnectionError = defaultValue(
          healthcheckResponse.getKeyManagerConnectivityError());

      overallStatus = isPodUp
          && healthcheckResponse.getAgentServiceUser()
          && healthcheckResponse.getPodConnectivity()
          && healthcheckResponse.getKeyManagerConnectivity();
    }
  }

  private String convert(boolean value) {
    return value ? CONNECTED : DISCONNECTED;
  }

  private String defaultValue(String originalValue) {
    return originalValue != null ? originalValue : NOT_AVAILABLE;
  }

  public boolean checkOverallStatus() {
    return overallStatus;
  }

}
