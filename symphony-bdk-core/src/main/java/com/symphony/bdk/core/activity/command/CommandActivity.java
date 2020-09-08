package com.symphony.bdk.core.activity.command;

import static com.symphony.bdk.core.service.datafeed.util.RealTimeEventsBinder.bindOnMessageSent;

import com.symphony.bdk.core.activity.exception.FatalActivityExecutionException;
import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * A form reply activity corresponds to any message send in a chat where the bot is part of.
 */
@Slf4j
@API(status = API.Status.STABLE)
public abstract class CommandActivity<C extends CommandContext> extends AbstractActivity<V4MessageSent, C> {

  /** Used to parse PresentationML message */
  private static final DocumentBuilder DOCUMENT_BUILDER = initDocumentBuilder();

  /** The bot display name */
  @Getter @Setter private String botDisplayName;

  /** {@inheritDoc} */
  @Override
  protected void bindToRealTimeEventsSource(Consumer<RealTimeEventListener> realTimeEventsSource) {
    bindOnMessageSent(realTimeEventsSource, this::processEvent);
  }

  /** {@inheritDoc} */
  protected void beforeMatcher(C context) {
    context.setTextContent(getMessageTextContext(context.getSourceEvent().getMessage().getMessage()));
  }

  protected String getMessageTextContext(String presentationML) {
    try {
      final Document doc = DOCUMENT_BUILDER.parse(new ByteArrayInputStream(presentationML.getBytes(StandardCharsets.UTF_8)));
      return doc.getChildNodes().item(0).getTextContent();
    } catch (SAXException | IOException e) {
      throw new FatalActivityExecutionException(this.getInfo(), "Unable to parse presentationML", e);
    }
  }

  @Nonnull
  @SneakyThrows
  private static DocumentBuilder initDocumentBuilder() {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    return factory.newDocumentBuilder();
  }
}
