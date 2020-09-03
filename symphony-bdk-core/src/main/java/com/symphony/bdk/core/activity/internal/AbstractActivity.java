package com.symphony.bdk.core.activity.internal;

import com.symphony.bdk.core.activity.Activity;
import com.symphony.bdk.core.activity.ActivityContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.gen.api.model.V4Initiator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

/**
 * Base abstract class for activities provided by the BDK. Provides a generic flow to process an incoming chat event.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public abstract class AbstractActivity<E, C extends ActivityContext<E>> implements Activity<C> {

  private ActivityInfo info;

  /**
   * Build activity info.
   */
  protected abstract ActivityInfo info();

  @Override
  public ActivityInfo getInfo() {

    if (this.info == null) {
      this.info = info();
    }

    return this.info;
  }

  /**
   * This callback can be used to prepare {@link ActivityContext} before actually processing the
   * {@link com.symphony.bdk.core.activity.ActivityMatcher#matches(ActivityContext)} method.
   * <p>
   *   WARNING: Please don't forget to call super if overridden.
   * </p>
   */
  protected void beforeMatcher(C context) {
    // nothing is done here by default
  }

  protected void processEvent(V4Initiator initiator, E event) {

    final C context = this.createContextInstance(initiator, event);

    try {
      log.trace("Before beforeMatcher execution");
      this.beforeMatcher(context);
    } catch (Exception ex) {
      log.warn("Before matcher execution failed.", ex);
    }

    // executes matcher with no failure
    final Optional<Boolean> matcherResult = this.executeMatcher(context);
    if (matcherResult.isPresent() && matcherResult.get()) {
      try {
        log.trace("Before activity execution");
        this.onActivity(context);
      } catch (Exception ex) {
        log.warn("Activity execution failed.", ex);
      }
    }
  }

  private Optional<Boolean> executeMatcher(C context) {
    try {
      log.trace("Before matcher execution");
      return Optional.of(this.matcher().matches(context));
    } catch (Exception ex) {
      log.warn("Matcher execution failed.", ex);
      return Optional.empty();
    }
  }

  @SneakyThrows // we can assume that this method can neven fail
  @SuppressWarnings("unchecked")
  private C createContextInstance(V4Initiator initiator, E event) {
    final Class<C> clz = (Class<C>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    return clz.getConstructor(V4Initiator.class, event.getClass()).newInstance(initiator, event);
  }
}
