package com.symphony.bdk.examples;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.gen.api.model.V4ImportResponse;
import com.symphony.bdk.gen.api.model.V4ImportedMessage;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.template.api.TemplateException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

/**
 * This demonstrates a basic usage of the message service.
 */
public class MessageExampleMain {

  public static final String STREAM_ID = "gXFV8vN37dNqjojYS_y2wX___o2KxfmUdA";
  public static final String MESSAGE = "<messageML>Hello, World!</messageML>";

  public static void main(String[] args)
      throws BdkConfigException, AuthUnauthorizedException, AuthInitializationException, TemplateException {
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    //send a regular message
    final V4Message regularMessage = bdk.messages().send(STREAM_ID, MESSAGE);

    //retrieve the details of an existing message
    final V4Message message = bdk.messages().getMessage("LE1TxlLArVbpNKn-CXZuZn___ouXdRpnbQ");

    //import a message
    V4ImportedMessage msg = new V4ImportedMessage();
    msg.setIntendedMessageFromUserId(12987981103694L);
    msg.setIntendedMessageTimestamp(1599481528000L);
    msg.setStreamId(STREAM_ID);
    msg.setMessage(MESSAGE);
    msg.setOriginatingSystemId("fooChat");
    final List<V4ImportResponse > v4ImportResponses = bdk.messages().importMessages(Collections.singletonList(msg));

    //use templates
    final V4Message sentWithBuiltInTemplate = bdk.messages().send(STREAM_ID, "simpleMML",
        new HashMap<String, String>() {{
      put("message", "Hello from a built-in template!");
    }});

    final V4Message sendWithCustomTemplate = bdk.messages()
        .send(STREAM_ID, "customTemplate.ftl", new HashMap<String, String>());
  }
}
