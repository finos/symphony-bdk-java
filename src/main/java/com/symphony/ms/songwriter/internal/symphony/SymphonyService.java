package com.symphony.ms.songwriter.internal.symphony;

import com.symphony.ms.songwriter.internal.symphony.model.AuthenticateResponse;
import com.symphony.ms.songwriter.internal.symphony.model.HealthCheckInfo;

import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;

public interface SymphonyService {

  void registerIMListener(IMListener imListener);

  void registerRoomListener(RoomListener roomListener);

  void registerElementsListener(ElementsListener elementsListener);

  Long getBotUserId();

  String getBotDisplayName();

  HealthCheckInfo healthCheck();

  void sendMessage(String streamId, String message, String jsonData);

  AuthenticateResponse appAuthenticate(String appId);

  boolean validateTokens(String appToken, String symphonyToken);

  Long verifyJWT(String jwt);

}
