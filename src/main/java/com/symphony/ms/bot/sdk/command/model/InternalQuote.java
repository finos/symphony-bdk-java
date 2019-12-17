package com.symphony.ms.bot.sdk.command.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sample code. Internal quote model used in QuoteCommandHandler.
 *
 */
@Data
@NoArgsConstructor
public class InternalQuote {

  private String from;
  private String fromName;
  private String to;
  private String toName;
  private Float rate;

  public InternalQuote(Quote quote) {
    from = quote.getFromCurrency();
    fromName = quote.getFromCurrencyName();
    to = quote.getToCurrency();
    toName = quote.getToCurrencyName();
    rate = quote.getExchangeRate();
  }

}
