package com.symphony.bdk.bot.sdk.symphony;

import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;

/**
 * Registers listeners for Symphony events
 *
 * @author msecato
 *
 */
public interface DatafeedClient {

  /**
   * Registers a listener for Symphony IM events
   *
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
   *
   * @param elementsListener
   */
  void registerElementsListener(ElementsListener elementsListener);

}
