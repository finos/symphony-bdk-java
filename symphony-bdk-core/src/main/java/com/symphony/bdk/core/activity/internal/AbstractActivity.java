package com.symphony.bdk.core.activity.internal;

import com.symphony.bdk.core.activity.Activity;
import com.symphony.bdk.core.activity.ActivityContext;
import com.symphony.bdk.gen.api.model.V4Initiator;

import lombok.SneakyThrows;
import org.apiguardian.api.API;

import java.lang.reflect.ParameterizedType;

/**
 * Base abstract class for activities provided by the BDK. Provides a generic flow to process an incoming chat event.
 */
@API(status = API.Status.INTERNAL)
public abstract class AbstractActivity<E, C extends ActivityContext<E>> implements Activity<C> {

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

    this.beforeMatcher(context);

    if (this.matcher().matches(context)) {
      this.onActivity(context);
    }
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  private C createContextInstance(V4Initiator initiator, E event) {
    final Class<C> clz = (Class<C>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    return clz.getConstructor(V4Initiator.class, event.getClass()).newInstance(initiator, event);
  }
}
