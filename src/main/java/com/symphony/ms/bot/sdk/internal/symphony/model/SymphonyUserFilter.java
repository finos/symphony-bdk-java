package com.symphony.ms.bot.sdk.internal.symphony.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Symphony user filter
 *
 * @author Gabriel Berberian
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SymphonyUserFilter {

  String query;
  boolean local;
  int skip;
  int limit;
  private String title;
  private String location;
  private String company;
}
