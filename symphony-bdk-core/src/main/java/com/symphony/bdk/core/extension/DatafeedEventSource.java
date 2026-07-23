package com.symphony.bdk.core.extension;

import com.symphony.bdk.gen.api.model.V4Event;

import org.apiguardian.api.API;

import java.util.List;

/**
 * SPI for replacing the datafeed read/ack cycle in {@code DatafeedLoopV2}.
 *
 * <p>This source is stateless — there is no persistent datafeed ID. The loop begins with a
 * {@code null} ackId on the first iteration and uses the ackId returned by
 * {@link #ackEvents(List)} on all subsequent iterations.
 *
 * <p>Exceptions thrown by {@link #readEvents(String)} are subject to the same retry policy as the
 * standard agent datafeed read.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface DatafeedEventSource {

  List<V4Event> readEvents(String ackId) throws Exception;

  String ackEvents(List<V4Event> events) throws Exception;
}
