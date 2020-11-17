package com.symphony.bdk.spring.events;

import com.symphony.bdk.gen.api.model.V4Initiator;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.util.Objects;

/**
 * Specific {@link ApplicationEvent} used to wrap Real Time Events that are then dispatched by the {@link RealTimeEventsDispatcher}.
 */
@Getter
public class RealTimeEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {

  /** Event initiator, or user that triggered it */
  private final V4Initiator initiator;
  /** Event payload */
  private final T source;

  public RealTimeEvent(V4Initiator initiator, T source) {
    super(source);
    this.initiator = initiator;
    this.source = source;
  }

  /**
   * Needed in order to be able to discriminate event listeners based on specific source types {@link T}
   */
  @Override
  public ResolvableType getResolvableType() {
    return ResolvableType.forClassWithGenerics(getClass(),
        ResolvableType.forInstance(source));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RealTimeEvent<?> that = (RealTimeEvent<?>) o;
    return initiator.equals(that.initiator) &&
        source.equals(that.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(initiator, source);
  }
}
