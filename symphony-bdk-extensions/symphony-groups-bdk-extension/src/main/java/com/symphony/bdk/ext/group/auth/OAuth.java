package com.symphony.bdk.ext.group.auth;

import com.symphony.bdk.http.api.Pair;

import com.symphony.bdk.http.api.auth.Authentication;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@API(status = API.Status.INTERNAL)
public class OAuth implements Authentication {

  private String bearerToken;

  @Override
  public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams) {
    if (this.bearerToken != null) {
      headerParams.put("Authorization", "Bearer " + this.bearerToken);
    }
  }
}
