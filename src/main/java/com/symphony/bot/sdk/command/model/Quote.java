package com.symphony.bot.sdk.command.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sample code. Quote model used in QuoteCommandHandler.
 *
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote {
  @JsonProperty("1. From_Currency Code")
  private String fromCurrency;

  @JsonProperty("2. From_Currency Name")
  private String fromCurrencyName;

  @JsonProperty("3. To_Currency Code")
  private String toCurrency;

  @JsonProperty("4. To_Currency Name")
  private String toCurrencyName;

  @JsonProperty("5. Exchange Rate")
  private Float exchangeRate;

  @JsonProperty("6. Last Refreshed")
  private String requestDate;

}
