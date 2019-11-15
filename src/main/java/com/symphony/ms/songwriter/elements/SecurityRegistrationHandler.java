package com.symphony.ms.songwriter.elements;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.elements.ElementsHandler;
import com.symphony.ms.songwriter.internal.event.model.SymphonyElementsEvent;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

import model.DropdownMenuOption;
import model.FormButtonType;
import utils.FormBuilder;

public class SecurityRegistrationHandler extends ElementsHandler {
  private static final String FORM_ID = "sec-register-form";

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " /register security$")
        .asPredicate();
  }

  @Override
  protected String getElementsFormId() {
    return FORM_ID;
  }

  @Override
  public void displayElements(BotCommand command,
      SymphonyMessage commandResponse) {

    String formML = FormBuilder.builder(FORM_ID)
        .addHeader(6, "Security Reference")
        .addTextField("secCode", "", "Enter a code..", true, false, 1, 15)
        .addHeader(6, "Assigned To:")
        .addPersonSelector("assignedTo", "Assign to..", false)
        .addHeader(6, "Trade Status:")
        .addRadioButton("status", "Pending", "pending", true)
        .addRadioButton("status", "Confirmed", "confirmed", false)
        .addRadioButton("status", "Settled", "settled", false)
        .addHeader(6, "Desk:")
        .addDropdownMenu("assetClass", false, Arrays.asList(
            new DropdownMenuOption("eq", "Equities", true),
            new DropdownMenuOption("fi", "Credit", false),
            new DropdownMenuOption("fx", "FX", false),
            new DropdownMenuOption("rates", "Rates", false)
        ))
        .addCheckBox("deliverable", "Non-Deliverable?", "nd", false)
        .addHeader(6, "Remarks:")
        .addTextArea("remarks", "", "Enter your remarks..", false)
        .addButton("confirm", "Confirm", FormButtonType.ACTION)
        .addButton("reset", "Reset", FormButtonType.RESET)
        .formatElement();

    commandResponse.setMessage(formML);
  }

  @Override
  public void handleAction(SymphonyElementsEvent event,
      SymphonyMessage elementsResponse) {
    elementsResponse.setMessage("Security registered successfully");
  }

}
