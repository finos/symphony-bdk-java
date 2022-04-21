package com.symphony.bdk.spring.service;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.tracing.MDCUtils;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.annotation.PostConstruct;

@Slf4j
@API(status = API.Status.INTERNAL)
public abstract class LoopAsyncLauncherService implements Thread.UncaughtExceptionHandler {
  private DatafeedLoop loop;
  private List<RealTimeEventListener> realTimeEventListeners;

  public LoopAsyncLauncherService(DatafeedLoop loop,
      List<RealTimeEventListener> realTimeEventListeners) {
    this.loop = loop;
    this.realTimeEventListeners = realTimeEventListeners;
  }

  /**
   * Registers all available {@link RealTimeEventListener} retrieved from the Spring application context.
   */
  @PostConstruct
  public void registerListeners() {
    this.realTimeEventListeners.forEach(this.loop::subscribe);
  }

  /**
   * Asynchronous execution of the {@link DatafeedLoop#start()} method.
   */
  public void start() {
    final Thread datafeedThread = new Thread(MDCUtils.wrap(this::uncheckedStart), "SymphonyBdk_" + getLoopType());
    datafeedThread.setUncaughtExceptionHandler(this);
    datafeedThread.start();
  }

  /**
   * Wrapper for the {@link DatafeedLoop#stop()} method.
   */
  public void stop() {
    this.loop.stop();
  }

  @Override
  public void uncaughtException(Thread datafeedThread, Throwable source) {
    final Throwable cause = source.getCause();

    if (cause.getClass().equals(AuthUnauthorizedException.class)) {
      log.error("An authentication exception has occurred while starting the {} loop, "
          + "please check error below:", getLoopType(), cause);
    } else if (cause.getClass().equals(ApiException.class)) {
      log.error("An API error has been received while starting the {} loop in a separate thread, "
          + "please check error below:", getLoopType(), cause);
    } else if (cause.getCause().getClass().equals(ConnectException.class) ||
        cause.getCause().getClass().equals(SocketTimeoutException.class)) {
      log.error("A Network error has occurred while starting the {} loop, "
          + "please check error below:", getLoopType(), source);
    } else {
      log.error("An unknown error has occurred while starting the {} loop, "
          + "please check error below:", getLoopType(), cause);
    }
  }

  /**
   *
   * @return the loop type, e.g. datafeed or datahose
   */
  protected abstract String getLoopType();

  /**
   * Wraps the {@link DatafeedLoop#start()} by encapsulating the potential {@link AuthUnauthorizedException} and
   * {@link ApiException} as a {@link RuntimeException}.
   */
  private void uncheckedStart() {
    try {
      this.loop.start();
    } catch (AuthUnauthorizedException | ApiException e) {
      throw new RuntimeException(e);
    }
  }
}
