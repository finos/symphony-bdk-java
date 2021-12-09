package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

/**
 * Interface representing a slash command token which is an argument, like {argument}.
 */
@API(status = API.Status.INTERNAL)
public interface ArgumentCommandToken extends CommandToken {

  /**
   *
   * @return the argument name.
   */
  String getArgumentName();
}
