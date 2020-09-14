package com.symphony.bdk.bot.sdk.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.User;

/**
 * Symphony Connection request accepted event
 * 
 * @author anthony.lee
 */
@Data
@NoArgsConstructor
public class ConnectionAcceptedEvent extends BaseEvent{
  private Long userId;

  public ConnectionAcceptedEvent(User user) {
    this.userId = user.getUserId();
  }
}
