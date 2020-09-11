package com.symphony.bdk.bot.sdk.symphony;

import listeners.ConnectionListener;
import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;

/**
 * Registers listeners for Symphony events
 *
 * @author Marcus Secato
 *
 */
public interface DatafeedClient {

  /**
   * Registers a listener for Symphony IM events
   *
   * @param imListener the IM listener
   */
  void registerIMListener(IMListener imListener);

  /**
   * Registers a listener for Symphony room events
   *
   * @param roomListener the room listener
   */
  void registerRoomListener(RoomListener roomListener);

  /**
   * Registers a listener for Symphony Elements events
   *
   * @param elementsListener the elements listener
   */
  void registerElementsListener(ElementsListener elementsListener);
  
  /**
   * Registers a listener for Symphony Connection events
   *
   * @param connectionsListener the connections listener
   */
  void registerConnectionsListener(ConnectionListener connectionsListener);

}
