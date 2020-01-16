package com.symphony.ms.bot.sdk.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
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
  private static final String FROM_CURRENCY = "fromCurrency";
  private static final String TO_CURRENCY = "toCurrency";
  private static final String AMOUNT = "amount";
  private static final String ASSIGNED_TO = "assignedTo";

  /**
   * Used by CommandFilter to filter Symphony chat messages
   */
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /register quote$")
        .asPredicate();
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
    data.put("form_id", getElementsFormId());
    elementsResponse.setTemplateFile("quote-registration", data);
  }

  /**
   * Invoked when elements form is submitted
   */
  @Override
  public void handleAction(SymphonyElementsEvent event,
      SymphonyMessage elementsResponse) {
    Map<String, Object> formValues = event.getFormValues();

    Map<String, Object> data = new HashMap<String, Object>();
    data.put(FROM_CURRENCY, formValues.get(FROM_CURRENCY));
    data.put(TO_CURRENCY, formValues.get(TO_CURRENCY));
    data.put(AMOUNT, formValues.get(AMOUNT));
    data.put(ASSIGNED_TO, event.getUser().getDisplayName());

    elementsResponse.setTemplateMessage(
        "Quote FX {{fromCurrency}}-{{toCurrency}} {{amount}} sent to dealer {{assignedTo}}", data);
  }

}
