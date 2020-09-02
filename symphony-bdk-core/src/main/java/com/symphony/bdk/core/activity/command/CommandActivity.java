package com.symphony.bdk.core.activity.command;

import static com.symphony.bdk.core.service.datafeed.util.RealTimeEventsBinder.bindOnMessageSent;

import com.symphony.bdk.core.activity.internal.AbstractActivity;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
  public void subscribe(Consumer<RealTimeEventListener> subscriber) {
    bindOnMessageSent(subscriber, this::processEvent);
  }

  /** {@inheritDoc} */
  protected void beforeMatcher(C context) {
    context.setTextContent(getMessageTextContext(context.getEventSource().getMessage().getMessage()));
  }

  @SneakyThrows // TODO handle exception properly here
  protected static String getMessageTextContext(String presentationML) {
    final Document doc = DOCUMENT_BUILDER.parse(new ByteArrayInputStream(presentationML.getBytes(StandardCharsets.UTF_8)));
    return doc.getChildNodes().item(0).getTextContent();
  }

  @Nonnull
  private static DocumentBuilder initDocumentBuilder() {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      return factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("[fatal] Unable to create the DocumentBuilder for XML parsing.", e);
    }
  }
}
