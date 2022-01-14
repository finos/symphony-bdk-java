package com.symphony.bdk.ext.group.auth;

import com.symphony.bdk.http.api.auth.Authentication;

import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;

import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
@API(status = API.Status.INTERNAL)
public class OAuth implements Authentication {

  private final Supplier<String> bearerTokenSupplier;

  @Override
  public void apply(final Map<String, String> headerParams) {
    headerParams.put("Authorization", "Bearer " + this.bearerTokenSupplier.get());
  }
}
