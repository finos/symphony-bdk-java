package com.symphony.bdk.bot.sdk.symphony;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.symphony.bdk.bot.sdk.lib.restclient.RestClient;
import com.symphony.bdk.bot.sdk.symphony.model.HealthCheckInfo;

import clients.SymBotClient;
import model.HealthcheckResponse;

@Service
public class HealthcheckClientImpl implements HealthcheckClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(HealthcheckClientImpl.class);
  private static final String HEALTH_POD_ENDPOINT = "pod/v1/podcert";

  private SymBotClient symBotClient;
  private RestClient restClient;

  @Value("${management.symphony-api-client-version}")
  private String symphonyApiClientVersion;

  public HealthcheckClientImpl(SymBotClient symBotClient, RestClient restClient) {
    this.symBotClient = symBotClient;
    this.restClient = restClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HealthCheckInfo healthCheck() {
    return new HealthCheckInfo(checkAgentStatus(),
        checkPodStatus(), symphonyApiClientVersion);
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
