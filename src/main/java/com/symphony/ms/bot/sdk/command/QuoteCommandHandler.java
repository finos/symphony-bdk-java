package com.symphony.ms.bot.sdk.command;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.symphony.ms.bot.sdk.command.model.InternalQuote;
import com.symphony.ms.bot.sdk.command.model.QuoteResponse;
import com.symphony.ms.bot.sdk.internal.command.CommandHandler;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.lib.restclient.RestClient;
import com.symphony.ms.bot.sdk.internal.lib.restclient.model.RestResponse;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;

/**
 * Sample code. CommandHandler that uses {@link RestClient} to consume external API to get currency
 * quotes.
 */
public class QuoteCommandHandler extends CommandHandler {

  private static final String QUOTE_URL =
      "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=USD"
          + "&to_currency=%s&apikey=C7G0Q2QOJ80OECGM";
  private static final String QUOTE_COMMAND = "/quote";

  private RestClient restClient;

  public QuoteCommandHandler(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " " + QUOTE_COMMAND)
        .asPredicate();
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    Optional<String> currency = getCommandCurrency(command.getMessage());
    if (currency.isPresent()) {
      RestResponse<QuoteResponse> response = requestQuote(currency.get());
      if (response.getStatus() == 200) {
        QuoteResponse quoteResponse = response.getBody();
        InternalQuote iQuote = new InternalQuote(quoteResponse.getQuote());
        commandResponse.setEnrichedTemplateFile("quote-result", iQuote,
            "com.symphony.ms.currencyQuote", iQuote, "1.0");
      }
    } else {
      commandResponse.setMessage("Please provide the currency you want quote for");
    }
  }

  private Optional<String> getCommandCurrency(String commandMessage) {
    String[] commandSplit = commandMessage.split(" " + QUOTE_COMMAND + " ");
    if (commandSplit.length > 1) {
      String currency = commandSplit[1];
      if (currency != null) {
        return Optional.of(currency);
      }
    }
    return Optional.empty();
  }

  private RestResponse<QuoteResponse> requestQuote(String currency) {
    return restClient.getRequest(
        String.format(QUOTE_URL, currency), QuoteResponse.class);
  }

}
