package com.symphony.bdk.core.auth;
import com.symphony.bdk.http.api.auth.Authentication;

import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;

import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
@API(status = API.Status.INTERNAL)
public class OAuthentication implements Authentication {

  private final Supplier<String> bearerAuthSupplier;

  @Override
  public void apply(Map<String, String> headerParams) {
    headerParams.remove("sessionToken");
    headerParams.put("Authorization", this.bearerAuthSupplier.get());
  }
}
