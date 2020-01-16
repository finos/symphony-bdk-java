package com.symphony.ms.bot.sdk.internal.symphony;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.symphony.ms.bot.sdk.internal.lib.restclient.RestClient;
import com.symphony.ms.bot.sdk.internal.symphony.model.HealthCheckInfo;
import clients.SymBotClient;
import model.HealthcheckResponse;

@Service
public class HealthcheckClientImpl implements HealthcheckClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(HealthcheckClientImpl.class);
  private static final String HEALTH_POD_ENDPOINT = "pod/v1/podcert";

  private SymBotClient symBotClient;
  private RestClient restClient;

  public HealthcheckClientImpl(SymBotClient symBotClient, RestClient restClient) {
    this.symBotClient = symBotClient;
    this.restClient = restClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HealthCheckInfo healthCheck() {
    return new HealthCheckInfo(checkAgentStatus(), checkPodStatus());
  }

  private String getPodHealthUrl() {
    String hostUrl = symBotClient.getConfig().getPodHost();

    return (hostUrl.startsWith("https://") ? "" : "https://")
        + (hostUrl.endsWith("/") ? hostUrl : hostUrl + "/") + HEALTH_POD_ENDPOINT;
  }

  private boolean checkPodStatus() {
    boolean isPodUp = false;
    try {
      restClient.getRequest(getPodHealthUrl(), String.class);
      isPodUp = true;
    } catch (Exception e) {
      LOGGER.error("Error getting pod health status", e);
    }

    return isPodUp;
  }

  private HealthcheckResponse checkAgentStatus() {
    try {
      return symBotClient.getHealthcheckClient().performHealthCheck();
    } catch (Exception e) {
      LOGGER.error("Error getting agent health status");
    }
    return null;
  }

}
