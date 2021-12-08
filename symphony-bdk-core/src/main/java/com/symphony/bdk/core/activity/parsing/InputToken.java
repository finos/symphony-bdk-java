package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public interface InputToken<T> {

  T getContent();

  String getContentAsString();
}
