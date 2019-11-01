package com.symphony.ms.songwriter.command;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.symphony.ms.songwriter.command.model.InternalQuote;
import com.symphony.ms.songwriter.command.model.QuoteResponse;
import com.symphony.ms.songwriter.internal.command.CommandHandler;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.lib.restclient.RestClient;
import com.symphony.ms.songwriter.internal.lib.restclient.model.RestResponse;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public class QuoteCommandHandler extends CommandHandler {

  private static final String QUOTE_URL = "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=USD&to_currency=%s&apikey=C7G0Q2QOJ80OECGM";
  private static final String QUOTE_COMMAND = "\\/quote";

  private RestClient restClient;

  public QuoteCommandHandler(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " " + QUOTE_COMMAND)
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    String[] commandSplit = command.getMessage().split(" " + QUOTE_COMMAND + " ");

    if (commandSplit.length > 1) {
      String currency = commandSplit[1];
      if (currency != null) {
        RestResponse<QuoteResponse> response = restClient.getRequest(
            String.format(QUOTE_URL, currency), QuoteResponse.class);

        if (response.getStatus() == 200) {
          QuoteResponse test = response.getBody();
          InternalQuote iQuote = new InternalQuote(test.getQuote());
          commandResponse.setEnrichedTemplateFile("quote-result.ftl", iQuote,
              "com.symphony.ms.devtools.currencyQuote", iQuote, "1.0");
        }
      }
    } else {
      commandResponse.setMessage("Please provide the currency you want quote for");
    }

  }

}
