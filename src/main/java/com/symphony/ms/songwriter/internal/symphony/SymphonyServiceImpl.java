package com.symphony.ms.songwriter.internal.symphony;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.symphony.ms.songwriter.internal.lib.restclient.RestClient;
import com.symphony.ms.songwriter.internal.symphony.model.AuthenticateResponse;
import com.symphony.ms.songwriter.internal.symphony.model.HealthCheckInfo;
import authentication.SymExtensionAppRSAAuth;
import clients.SymBotClient;
import listeners.IMListener;
import listeners.RoomListener;
import model.AppAuthResponse;
import model.HealthcheckResponse;
import model.OutboundMessage;
import model.UserInfo;

@Service
public class SymphonyServiceImpl implements SymphonyService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SymphonyServiceImpl.class);

  private static final String HEALTH_POD_ENDPOINT = "pod/v1/podcert";

  // TODO: add protection to all calls to Symphony SDK that may raise exception
  private SymBotClient symBotClient;

  private SymExtensionAppRSAAuth symExtensionAppRSAAuth;

  private RestClient restClient;

  public SymphonyServiceImpl(SymBotClient symBotClient,
      SymExtensionAppRSAAuth symExtensionAppRSAAuth, RestClient restClient) {
    this.symBotClient = symBotClient;
    this.symExtensionAppRSAAuth = symExtensionAppRSAAuth;
    this.restClient = restClient;
  }

  @Override
  public void registerIMListener(IMListener imListener) {
    LOGGER.info("Adding IM listener");
    symBotClient.getDatafeedEventsService().addIMListener(imListener);
  }

  @Override
  public void registerRoomListener(RoomListener roomListener) {
    LOGGER.info("Adding Room listener");
    symBotClient.getDatafeedEventsService().addRoomListener(roomListener);
  }

  @Override
  public Long getBotUserId() {
    return symBotClient.getBotUserInfo().getId();
  }

  @Override
  public String getBotDisplayName() {
    return symBotClient.getBotUserInfo().getDisplayName();
  }

  @Override
  public HealthCheckInfo healthCheck() {
    return new HealthCheckInfo(checkAgentStatus(), checkPodStatus());
  }

  @Override
  public void sendMessage(String streamId, String message, String jsonData) {
    OutboundMessage outMessage = null;
    if (jsonData == null) {
      outMessage = new OutboundMessage(message);
    } else {
      outMessage = new OutboundMessage(message, jsonData);
    }

    internalSendMessage(streamId, outMessage);
  }

  @Override
  public AuthenticateResponse appAuthenticate(String appId) {
    try {
      AppAuthResponse appAuthToken = symExtensionAppRSAAuth.appAuthenticate();
      return new AuthenticateResponse(appId, appAuthToken.getAppToken(),
          appAuthToken.getSymphonyToken());
    } catch (Exception e) {
      LOGGER.error("Error authentication extension app: {}\n{}", appId, e);
      throw new AppAuthenticateException();
    }
  }

  @Override
  public boolean validateTokens(String appToken, String symphonyToken) {
    return symExtensionAppRSAAuth.validateTokens(appToken, symphonyToken);
  }

  @Override
  public Long verifyJWT(String jwt) {
    UserInfo userInfo = symExtensionAppRSAAuth.verifyJWT(jwt);
    if (userInfo != null) {
      return userInfo.getId();
    }
    throw new AppAuthenticateException();
  }

  private String getPodHealthUrl() {
    String hostUrl = symBotClient.getConfig().getPodHost();

    return (hostUrl.startsWith("https://") ? "" : "https://")
        + (hostUrl.endsWith("/") ? hostUrl : hostUrl + "/") + HEALTH_POD_ENDPOINT;
  }

  private boolean checkPodStatus() {
    boolean isPodUp = false;
    try {
      restClient.getRequest(getPodHealthUrl());
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

  private void internalSendMessage(String streamId, OutboundMessage message) {
    LOGGER.debug("Sending message to stream: {}", streamId);
    try {
      symBotClient.getMessagesClient().sendMessage(streamId, message);
    } catch (Exception e) {
      LOGGER.error("Error sending message to stream: {}\n{}", streamId, e);
      throw new SendMessageException();
    }
  }

}
