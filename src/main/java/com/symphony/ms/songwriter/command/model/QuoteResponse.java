package com.symphony.ms.songwriter.command.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sample code. Quote response model used in QuoteCommandHandler.
 *
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteResponse {

  @JsonProperty("Realtime Currency Exchange Rate")
  private Quote quote;

}
