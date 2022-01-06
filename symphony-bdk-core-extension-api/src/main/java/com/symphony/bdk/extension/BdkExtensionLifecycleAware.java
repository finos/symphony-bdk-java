package com.symphony.bdk.extension;

public interface BdkExtensionLifecycleAware {

  void start();

  default void stop() {}
}
