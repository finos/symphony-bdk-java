package com.symphony.bdk.core.command.event;

import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;

/**
 * TODO: add description here
 */
public interface CommandEventType<T> {

  RealTimeEventListener listener();

  Class<T> eventPayloadType();
}
