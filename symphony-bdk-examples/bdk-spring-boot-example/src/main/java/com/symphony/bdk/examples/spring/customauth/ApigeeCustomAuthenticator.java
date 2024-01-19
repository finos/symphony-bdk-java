package com.symphony.bdk.examples.spring.customauth;

import com.symphony.bdk.core.auth.impl.AbstractCustomAuthenticator;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.http.api.ApiException;

import org.springframework.stereotype.Component;

import jakarta.annotation.Nonnull;

import java.util.List;

@Component
public class ApigeeCustomAuthenticator extends AbstractCustomAuthenticator {

  public ApigeeCustomAuthenticator(BdkConfig bdkConfig) {
    super(bdkConfig);
  }

  @Override
  protected @Nonnull String doRetrieveToken() throws ApiException {
    return "Bearer token";
  }

  @Override
  public boolean isAuthTokenExpired(ApiException exception) {
    String responseBody = exception.getResponseBody();
    List<String> defaultFaultHeader = exception.getResponseHeaders().get("DefaultFaultHeader");
    if (defaultFaultHeader != null && defaultFaultHeader.contains("invalid_acccess_token") && responseBody.contains(
        "IGTWY") && responseBody.contains("Unauthorized")) {
      return true;
    }
    return false;
  }
}
