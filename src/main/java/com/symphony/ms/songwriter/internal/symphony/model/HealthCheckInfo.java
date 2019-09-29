package com.symphony.ms.songwriter.internal.symphony.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.HealthcheckResponse;

@Data
@NoArgsConstructor
public class HealthCheckInfo {
  private String podVersion;
  private String agentVersion;
  private boolean agentConnectivity;
  private boolean agentPodConnectivity;
  private boolean agentKeyManagerConnectivity;
  private String agentPodConnectivityError;
  private String agentKeyManagerConnectivityError;
  private boolean podConnectivity;


  public HealthCheckInfo(HealthcheckResponse healthcheckResponse, boolean isPodUp) {
    if (healthcheckResponse != null) {
      this.setPodVersion(healthcheckResponse.getPodVersion());
      this.setAgentVersion(healthcheckResponse.getAgentVersion());
      this.setAgentConnectivity(healthcheckResponse.getAgentServiceUser());
      this.setAgentPodConnectivity(healthcheckResponse.getPodConnectivity());
      this.setAgentPodConnectivityError(healthcheckResponse.getPodConnectivityError());
      this.setAgentKeyManagerConnectivity(healthcheckResponse.getKeyManagerConnectivity());
      this.setAgentKeyManagerConnectivityError(healthcheckResponse.getKeyManagerConnectivityError());
    }
    this.setPodConnectivity(isPodUp);
  }

}
