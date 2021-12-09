package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public interface CommandToken {

  boolean matches(Object inputToken);
}
