package com.symphony.bdk.ext.group.auth;

import com.symphony.bdk.http.api.auth.Authentication;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.Map;
import java.util.function.Supplier;

@Getter
@Setter
@API(status = API.Status.INTERNAL)
public class OAuth implements Authentication {

  private Supplier<String> bearerTokenSupplier = () -> "";

  @Override
  public void apply(final Map<String, String> headerParams) {
    if (this.bearerTokenSupplier != null) {
      headerParams.put("Authorization", "Bearer " + this.bearerTokenSupplier.get());
    }
  }
}
