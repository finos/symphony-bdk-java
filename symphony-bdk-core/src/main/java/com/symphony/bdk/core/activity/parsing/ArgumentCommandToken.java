package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public interface ArgumentCommandToken extends CommandToken {

  String getArgumentName();
}
