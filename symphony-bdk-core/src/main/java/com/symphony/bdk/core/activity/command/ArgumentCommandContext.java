package com.symphony.bdk.core.activity.command;

import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.Collections;
import java.util.Map;

@API(status = API.Status.EXPERIMENTAL)
public class ArgumentCommandContext extends CommandContext {

  @Getter @Setter
  private Map<String, String> arguments;

  public ArgumentCommandContext(V4Initiator initiator, V4MessageSent eventSource) {
    super(initiator, eventSource);
    this.arguments = Collections.emptyMap();
  }
}
