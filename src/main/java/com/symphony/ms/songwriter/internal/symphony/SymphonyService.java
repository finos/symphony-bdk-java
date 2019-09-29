package com.symphony.ms.songwriter.internal.symphony;

import com.symphony.ms.songwriter.internal.symphony.model.AuthenticateResponse;
import com.symphony.ms.songwriter.internal.symphony.model.HealthCheckInfo;
import listeners.IMListener;
import listeners.RoomListener;

public interface SymphonyService {

  // TODO: review methods in PagerDuty

  void registerIMListener(IMListener imListener);

  void registerRoomListener(RoomListener roomListener);

  Long getBotUserId();

  String getBotDisplayName();

  HealthCheckInfo healthCheck();

  void sendMessage(String streamId, String message, String jsonData);

  AuthenticateResponse appAuthenticate(String appId);

  boolean validateTokens(String appToken, String symphonyToken);

  Long verifyJWT(String jwt);

}
