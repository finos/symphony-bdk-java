package com.symphony.bdk.http.api.auth;

import com.symphony.bdk.http.api.Pair;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class OAuth implements Authentication {

  private String bearerToken;
  private boolean isCommonJwtEnabled;

  @Override
  public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams) {
    if (this.bearerToken != null && this.isCommonJwtEnabled) {
      if (this.bearerToken.startsWith("Bearer ")) {
        headerParams.put("Authorization", this.bearerToken);
      } else {
        headerParams.put("Authorization", "Bearer " + this.bearerToken);
      }
      headerParams.remove("sessionToken");
    }
  }
}

