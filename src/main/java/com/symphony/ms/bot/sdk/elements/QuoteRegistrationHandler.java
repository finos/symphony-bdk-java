package com.symphony.ms.bot.sdk.elements;

import static com.symphony.ms.bot.sdk.internal.command.matcher.CommandMatcherBuilder.beginsAndEndsWith;
import static com.symphony.ms.bot.sdk.internal.command.matcher.EscapedCharacter.whiteSpace;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import com.symphony.ms.bot.sdk.internal.command.matcher.CommandMatcherBuilder;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.elements.ElementsHandler;
import com.symphony.ms.bot.sdk.internal.event.model.SymphonyElementsEvent;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;

/**
 * Sample code. Implementation of {@link ElementsHandler} which renders a Symphony elements form and
 * handles its submission.
 */
public class QuoteRegistrationHandler extends ElementsHandler {
  private static final String FORM_ID = "quo-register-form";

  /**
   * Used by CommandFilter to filter Symphony chat messages
   */
  @Override
  protected Predicate<String> getCommandMatcher() {
    return beginsAndEndsWith(
        new CommandMatcherBuilder()
            .followedBy("@")
            .followedBy(getBotName())
            .followedBy(whiteSpace())
            .followedBy("/register")
            .followedBy(whiteSpace())
            .followedBy("quote")
    ).predicate();
  }

  @Override
  protected String getElementsFormId() {
    return FORM_ID;
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void displayElements(BotCommand command,
      SymphonyMessage elementsResponse) {
    Map<String, String> data = new HashMap<>();
    data.put("form_id", FORM_ID);
    elementsResponse.setTemplateFile("quote-registration", data);
  }

  /**
   * Invoked when elements form is submitted
   */
  @Override
  public void handleAction(SymphonyElementsEvent event,
      SymphonyMessage elementsResponse) {
    elementsResponse.setMessage("Quote registered successfully");
  }

}
