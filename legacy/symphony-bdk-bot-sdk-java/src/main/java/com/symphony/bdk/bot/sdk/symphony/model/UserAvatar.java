package com.symphony.bdk.bot.sdk.symphony.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import model.Avatar;

/**
 * User avatar
 *
 * @author Gabriel Berberian
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAvatar {

  private String size;
  private String url;

  public UserAvatar(Avatar avatar) {
    this.size = avatar.getSize();
    this.url = avatar.getUrl();
  }

}
