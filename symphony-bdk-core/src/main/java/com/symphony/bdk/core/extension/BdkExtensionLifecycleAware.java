package com.symphony.bdk.core.extension;

public interface BdkExtensionLifecycleAware {

  void start();

  default void stop() {}
}
