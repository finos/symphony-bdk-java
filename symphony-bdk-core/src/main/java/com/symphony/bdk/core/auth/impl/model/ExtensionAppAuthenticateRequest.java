package com.symphony.bdk.core.auth.impl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.symphony.bdk.gen.api.model.AuthenticateRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * Request body for extension app authentication
 * FIXME Temporary class until swagger specs are fixed (PLAT-9557)
 */
@ApiModel(description = "Request body for extension app authentication")
@JsonPropertyOrder({
    ExtensionAppAuthenticateRequest.JSON_PROPERTY_APP_TOKEN
})

public class ExtensionAppAuthenticateRequest extends AuthenticateRequest {
  public static final String JSON_PROPERTY_APP_TOKEN = "appToken";
  private String appToken;

  public ExtensionAppAuthenticateRequest appToken(String appToken) {
    this.appToken = appToken;
    return this;
  }

  @JsonIgnore
  public String getToken() {
    return null;
  }

  /**
   * application generated token
   * @return appToken
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "application generated token")
  @JsonProperty(JSON_PROPERTY_APP_TOKEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getAppToken() {
    return appToken;
  }

  public void setAppToken(String appToken) {
    this.appToken = appToken;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExtensionAppAuthenticateRequest extensionAppAuthenticateRequest = (ExtensionAppAuthenticateRequest) o;
    return Objects.equals(this.appToken, extensionAppAuthenticateRequest.appToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(appToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExtensionAppAuthenticateRequest {\n");
    sb.append("    appToken: ").append(toIndentedString(appToken)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
