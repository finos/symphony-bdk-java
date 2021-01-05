package com.symphony.bdk.core.activity.command;

import com.symphony.bdk.core.activity.model.ActivityInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apiguardian.api.API;

/**
 * {@link com.symphony.bdk.core.activity.command.CommandActivity} information/documentation model.
 */
@Getter
@Setter
@Accessors(fluent = true)
@API(status = API.Status.EXPERIMENTAL)
public class CommandActivityInfo extends ActivityInfo {

  private String commandName;

  private String summary;
}
