package com.symphony.bdk.core.activity.form;

import static com.symphony.bdk.core.service.datafeed.util.RealTimeEventsBinder.bindOnSymphonyElementsAction;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.function.Consumer;

/**
 * A form reply activity corresponds to an Elements form submission.
 */
@Slf4j
@API(status = API.Status.STABLE)
public abstract class FormReplyActivity<C extends FormReplyContext>
    extends AbstractActivity<V4SymphonyElementsAction, C> {

  private static final ObjectMapper MAPPER = new JsonMapper();

  /** {@inheritDoc} */
  @Override
  protected void bindToRealTimeEventsSource(Consumer<RealTimeEventListener> realTimeEventsSource) {
    bindOnSymphonyElementsAction(realTimeEventsSource, this::processEvent);
  }

  /** {@inheritDoc} */
  @Override
  protected void beforeMatcher(C context) {
    super.beforeMatcher(context);
    // copy streamId to context root level
    context.setStreamId(context.getSourceEvent().getStream().getStreamId());
    // copy formMessageId to context root level
    context.setFormMessageId(context.getSourceEvent().getFormMessageId());
    // copy formId to context root level
    context.setFormId(context.getSourceEvent().getFormId());
    // setup formValues as a JsonNode
    context.setFormValues(MAPPER.valueToTree(context.getSourceEvent().getFormValues()));
  }
}
