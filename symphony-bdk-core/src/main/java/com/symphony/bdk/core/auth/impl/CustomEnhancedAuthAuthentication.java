package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.retry.function.SupplierWithApiException;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.auth.Authentication;

import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import java.util.Map;

@RequiredArgsConstructor
@API(status = Status.EXPERIMENTAL)
public class CustomEnhancedAuthAuthentication implements Authentication {
  private final String authHeaderName;
  private final SupplierWithApiException<String> tokenSupplier;

  @Override
  public void apply(Map<String, String> headerParams) throws ApiException {
    headerParams.put(authHeaderName, tokenSupplier.get());
  }
}
