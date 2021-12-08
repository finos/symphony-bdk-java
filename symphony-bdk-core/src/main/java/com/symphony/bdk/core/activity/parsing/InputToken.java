package com.symphony.bdk.core.activity.parsing;

public interface InputToken<T> {

  T getContent();

  String getContentAsString();
}
