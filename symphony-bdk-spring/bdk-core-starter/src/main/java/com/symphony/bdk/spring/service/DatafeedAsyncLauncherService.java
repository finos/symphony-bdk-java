package com.symphony.bdk.spring.service;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;

import java.util.List;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

/**
 *
 */
public class DatafeedAsyncLauncherService {

  private final DatafeedService datafeedService;
  private final List<RealTimeEventListener> realTimeEventListeners;

  public DatafeedAsyncLauncherService(DatafeedService datafeedService, List<RealTimeEventListener> realTimeEventListeners) {
    this.datafeedService = datafeedService;
    this.realTimeEventListeners = realTimeEventListeners;
  }

  /**
   * Registers all available {@link RealTimeEventListener} retrieved from the Spring application context.
   */
  @PostConstruct
  public void registerListeners() {
    this.realTimeEventListeners.forEach(this.datafeedService::subscribe);
  }

  /**
   * Asynchronous execution of the {@link DatafeedService#start()} method.
   */
  public void start() {
    Executors.newSingleThreadExecutor().submit(() -> {
      try {
        this.datafeedService.start();
      } catch (AuthUnauthorizedException | ApiException apiException) {
        apiException.printStackTrace();
      }
    });
  }

  public void stop() {
    this.datafeedService.stop();
  }
}
