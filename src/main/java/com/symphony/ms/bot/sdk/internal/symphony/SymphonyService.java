package com.symphony.ms.bot.sdk.internal.symphony;

import com.symphony.ms.bot.sdk.internal.symphony.model.AuthenticateResponse;
import com.symphony.ms.bot.sdk.internal.symphony.model.HealthCheckInfo;
import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;

/**
 * Exposes simple Symphony-specific services abstracting any complexities to
 * talk to Symphony APIs.
 *
 * @author Marcus Secato
 *
 */
public interface SymphonyService {

  /**
   * Registers a listener for Symphony IM events
   * @param imListener
   */
  void registerIMListener(IMListener imListener);

  /**
   * Registers a listener for Symphony room events
   *
   * @param roomListener
   */
  void registerRoomListener(RoomListener roomListener);

  /**
   * Registers a listener for Symphony Elements events
   * @param elementsListener
   */
  void registerElementsListener(ElementsListener elementsListener);

  /**
   * @return bot user id
   */
  Long getBotUserId();

  /**
   * @return bot display name
   */
  String getBotDisplayName();

  /**
   * Retrieves health details for Symphony components (e.g. POD, agent)
   *
   * @return health check info
   */
  HealthCheckInfo healthCheck();

  /**
   * Sends message to Symphony stream
   *
   * @param streamId
   * @param message
   * @param jsonData
   */
  void sendMessage(String streamId, String message, String jsonData);

  /**
   * Initializes the process of authenticating an extension app
   *
   * @param appId
   * @return authenticate response
   */
  AuthenticateResponse appAuthenticate(String appId);

  /**
   * Validates tokens as part of extension app authentication process
   *
   * @param appToken
   * @param symphonyToken
   * @return true if tokens are valid, false otherwise
   */
  boolean validateTokens(String appToken, String symphonyToken);

  /**
   * Verifies the given JWT
   *
   * @param jwt
   * @return userId
   */
  Long verifyJWT(String jwt);

}
