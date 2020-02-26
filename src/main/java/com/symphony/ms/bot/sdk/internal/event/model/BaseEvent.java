package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base Symphony event
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class BaseEvent {

  protected String streamId;
  protected Long userId;

}
