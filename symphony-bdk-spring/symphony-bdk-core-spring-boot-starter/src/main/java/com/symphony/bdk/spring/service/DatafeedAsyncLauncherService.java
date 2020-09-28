package com.symphony.bdk.spring.service;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.List;

import javax.annotation.PostConstruct;

/**
 * Async Launcher for the {@link DatafeedService} that call the {@link DatafeedService#start()} method in a separate
 * thread.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class DatafeedAsyncLauncherService implements Thread.UncaughtExceptionHandler {

  private final DatafeedService datafeedService;
  private final List<RealTimeEventListener> realTimeEventListeners;

  public DatafeedAsyncLauncherService(
      final DatafeedService datafeedService,
      final List<RealTimeEventListener> realTimeEventListeners
  ) {
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
    final Thread datafeedThread = new Thread(this::uncheckedStart, "SymphonyBdk_DatafeedThread");
    datafeedThread.setUncaughtExceptionHandler(this);
    datafeedThread.start();
  }

  /**
   * Wrapper for the {@link DatafeedService#stop()} method.
   */
  public void stop() {
    this.datafeedService.stop();
  }

  @Override
  public void uncaughtException(Thread datafeedThread, Throwable source) {

    final Throwable cause = source.getCause();

    if (cause.getClass().equals(AuthUnauthorizedException.class)) {
      log.error("An authentication exception has occurred while starting the Datafeed loop, "
          + "please check error below:", cause);
    } else if (cause.getClass().equals(ApiException.class)) {
      log.error("An API error has been received while starting the Datafeed loop in a separate thread, "
          + "please check error below:", cause);
    } else {
      log.error("An unknown error has occurred while starting the Datafeed loop, "
          + "please check error below:", cause);
    }
  }

  /**
   * Wraps the {@link DatafeedService#start()} by encapsulating the potential {@link AuthUnauthorizedException} and
   * {@link ApiException} as a {@link RuntimeException}.
   */
  private void uncheckedStart() {
    try {
      this.datafeedService.start();
    } catch (AuthUnauthorizedException | ApiException e) {
      throw new RuntimeException(e);
    }
  }
}
