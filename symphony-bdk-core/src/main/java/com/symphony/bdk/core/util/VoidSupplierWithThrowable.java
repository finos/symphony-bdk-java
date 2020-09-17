package com.symphony.bdk.core.util;

@FunctionalInterface
public interface VoidSupplierWithThrowable {
  void get() throws Throwable;
}
