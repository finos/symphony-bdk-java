package com.symphony.bdk.spring.service;

import com.symphony.bdk.core.service.datafeed.DatahoseLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DatahoseAsyncLauncherService extends LoopAsyncLauncherService {

  public DatahoseAsyncLauncherService(DatahoseLoop datahoseLoop,
      List<RealTimeEventListener> realTimeEventListeners) {
    super(datahoseLoop, realTimeEventListeners);
  }

  @Override
  protected String getLoopType() {
    return "Datahose";
  }
}
