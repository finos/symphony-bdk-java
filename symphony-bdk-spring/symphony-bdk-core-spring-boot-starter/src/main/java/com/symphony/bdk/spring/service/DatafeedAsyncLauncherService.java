package com.symphony.bdk.spring.service;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.tracing.MDCUtils;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.List;

import javax.annotation.PostConstruct;

/**
 * Async Launcher for the {@link DatafeedLoop} that call the {@link DatafeedLoop#start()} method in a separate
 * thread.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class DatafeedAsyncLauncherService implements Thread.UncaughtExceptionHandler {

  private final DatafeedLoop datafeedLoop;
  private final List<RealTimeEventListener> realTimeEventListeners;

  public DatafeedAsyncLauncherService(
      final DatafeedLoop datafeedLoop,
      final List<RealTimeEventListener> realTimeEventListeners
  ) {
    this.datafeedLoop = datafeedLoop;
    this.realTimeEventListeners = realTimeEventListeners;
  }

  /**
   * Registers all available {@link RealTimeEventListener} retrieved from the Spring application context.
   */
  @PostConstruct
  public void registerListeners() {
    this.realTimeEventListeners.forEach(this.datafeedLoop::subscribe);
  }

  /**
   * Asynchronous execution of the {@link DatafeedLoop#start()} method.
   */
  public void start() {
    final Thread datafeedThread = new Thread(MDCUtils.wrap(this::uncheckedStart), "SymphonyBdk_DatafeedThread");
    datafeedThread.setUncaughtExceptionHandler(this);
    datafeedThread.start();
  }

  /**
   * Wrapper for the {@link DatafeedLoop#stop()} method.
   */
  public void stop() {
    this.datafeedLoop.stop();
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
    } else if(source.getClass().equals(RuntimeException.class)){
      log.error(source.getMessage(), cause);
    } else {
      log.error("An unknown error has occurred while starting the Datafeed loop, "
          + "please check error below:", cause);
    }
  }

  /**
   * Wraps the {@link DatafeedLoop#start()} by encapsulating the potential {@link AuthUnauthorizedException} and
   * {@link ApiException} as a {@link RuntimeException}.
   */
  private void uncheckedStart() {
    try {
      this.datafeedLoop.start();
    } catch (AuthUnauthorizedException | ApiException e) {
      throw new RuntimeException(e);
    }
  }
}
