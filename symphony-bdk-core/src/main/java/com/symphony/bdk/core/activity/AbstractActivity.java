package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.datafeed.EventPayload;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 * Base abstract class for activities provided by the BDK. Provides a generic flow to process an incoming chat event.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public abstract class AbstractActivity<E, C extends ActivityContext<E>> {

  private ActivityInfo info;
  private final ExecutorService executorService;

  public AbstractActivity() {
    ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setName("Activity-Async-Thread")
        .setPriority(Thread.NORM_PRIORITY)
        .build();
    executorService = Executors.newCachedThreadPool(threadFactory);
  }

  /**
   * Any kind of activity must provide an {@link ActivityMatcher} in order to detect if it can be applied to a certain
   * user input.
   *
   * @return an {@link ActivityMatcher} implementation.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  protected abstract ActivityMatcher<C> matcher() throws EventException;

  /**
   * Contain the activity business logic. Executed only if the {@link ActivityMatcher#matches(ActivityContext)} returned
   * a true value.
   *
   * @param context The activity context object.
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  protected abstract void onActivity(C context) throws EventException;

  /**
   * Bind an Activity to its real-time event.
   *
   * @param realTimeEventsSource The real-time events source, issued from the {@link DatafeedLoop}.
   */
  protected abstract void bindToRealTimeEventsSource(Consumer<RealTimeEventListener> realTimeEventsSource);

  /**
   * Build activity info.
   */
  protected abstract ActivityInfo info();

  /**
   * Retrieve activity details. Can be used for metrics, reporting or help generation.
   *
   * @return activity info
   */
  public ActivityInfo getInfo() {

    if (this.info == null) {
      this.info = info();
    }

    return this.info;
  }

  protected boolean isAsynchronous() {
    return false;
  }

  /**
   * This callback can be used to prepare {@link ActivityContext} before actually processing the
   * {@link com.symphony.bdk.core.activity.ActivityMatcher#matches(ActivityContext)} method.
   *
   * @throws EventException Throw this exception if this method should fail the current events processing
   *                        and re-queue the events in datafeed. Other exceptions will be caught silently.
   */
  protected void beforeMatcher(C context) throws EventException {
    // nothing is done here by default
  }

  protected void processEvent(V4Initiator initiator, E event) throws EventException {

    final C context = this.createContextInstance(initiator, event);

    try {
      log.trace("Before beforeMatcher execution");
      this.beforeMatcher(context);
    } catch (EventException ex) {
      throw ex; // to allow events to be re-queued in DFv2 loop
    } catch (Exception ex) {
      log.warn("Before matcher execution failed.", ex);
    }

    // executes matcher with no failure
    final Optional<Boolean> matcherResult = this.executeMatcher(context);
    if (matcherResult.isPresent() && Boolean.TRUE.equals(matcherResult.get())) {
      if (isAsynchronous()) {
        executorService.submit(() -> executeActivity(context));
      } else {
        executeActivity(context);
      }
    }
  }

  private void executeActivity(C context) {
    try {
      log.trace("Before activity execution");
      this.onActivity(context);
    } catch (EventException ex) {
      throw ex; // to allow events to be re-queued in DFv2 loop
    } catch (Exception ex) {
      log.warn("Activity execution failed.", ex);
    }
  }

  private Optional<Boolean> executeMatcher(C context) {
    try {
      log.trace("Before matcher execution");
      return Optional.of(this.matcher().matches(context));
    } catch (EventException ex) {
      throw ex; // to allow events to be re-queued in DFv2 loop
    } catch (Exception ex) {
      log.warn("Matcher execution failed.", ex);
      return Optional.empty();
    }
  }

  @SneakyThrows // assuming that this method can never fail
  @SuppressWarnings("unchecked")
  protected C createContextInstance(V4Initiator initiator, E event) {
    final Class<C> clz = (Class<C>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    if (EventPayload.class.isAssignableFrom(event.getClass())) {
      return clz.getConstructor(V4Initiator.class, event.getClass().getSuperclass()).newInstance(initiator, event);
    }
    return clz.getConstructor(V4Initiator.class, event.getClass()).newInstance(initiator, event);
  }
}
