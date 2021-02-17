package com.symphony.bdk.bot.sdk.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.User;

/**
 * Symphony Connection request received event
 * 
 * @author anthony.lee
 */
@Data
@NoArgsConstructor
public class ConnectionRequestedEvent extends BaseEvent{
  private Long userId;

  public ConnectionRequestedEvent(User user) {
    this.userId = user.getUserId();
  }
}
