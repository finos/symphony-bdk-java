package com.symphony.bdk.core.auth;
import com.symphony.bdk.core.util.function.SupplierWithApiException;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.auth.Authentication;

import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;

import java.util.Map;

@RequiredArgsConstructor
@API(status = API.Status.INTERNAL)
public class OAuthentication implements Authentication {

  private final SupplierWithApiException<String> bearerAuthSupplier;

  @Override
  public void apply(Map<String, String> headerParams) throws ApiException {
    headerParams.remove("sessionToken");
    headerParams.put("Authorization", this.bearerAuthSupplier.get());
  }
}
