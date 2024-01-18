package com.symphony.bdk.examples.spring.customauth;

import com.symphony.bdk.core.auth.impl.AbstractCustomAuthenticator;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.http.api.ApiException;

import org.springframework.stereotype.Component;

import jakarta.annotation.Nonnull;

@Component
public class ApigeeCustomAuthenticator extends AbstractCustomAuthenticator {

  public ApigeeCustomAuthenticator(BdkConfig bdkConfig) {
    super(bdkConfig);
  }

  @Override
  protected @Nonnull String doRetrieveToken() throws ApiException {
    return "token";
  }
}
