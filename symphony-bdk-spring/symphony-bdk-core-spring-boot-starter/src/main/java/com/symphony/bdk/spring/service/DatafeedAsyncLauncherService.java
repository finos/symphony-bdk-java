package com.symphony.bdk.spring.service;

import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.List;

/**
 * Async Launcher for the {@link DatafeedLoop} that call the {@link DatafeedLoop#start()} method in a separate
 * thread.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class DatafeedAsyncLauncherService extends LoopAsyncLauncherService {
  public DatafeedAsyncLauncherService(
      final DatafeedLoop datafeedLoop,
      final List<RealTimeEventListener> realTimeEventListeners
  ) {
    super(datafeedLoop, realTimeEventListeners);
  }

  @Override
  protected String getLoopType() {
    return "Datafeed";
  }
}
