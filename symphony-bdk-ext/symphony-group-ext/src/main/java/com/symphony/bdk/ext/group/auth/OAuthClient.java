package com.symphony.bdk.ext.group.auth;

import static com.symphony.bdk.http.api.Pair.pair;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.util.TypeReference;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
@API(status = API.Status.INTERNAL)
public class OAuthClient {

  private final ApiClient loginClient;

  public String retrieveBearerToken(@Nonnull final AuthSession session) throws ApiException {

    final ApiResponse<TokenResponse> response = this.loginClient.invokeAPI(
        "/idm/tokens",
        "POST",
        singletonList(pair("scope", "profile-manager")),
        null,
        singletonMap("sessionToken", session.getSessionToken()),
        null,
        null,
        "application/json",
        "application/json",
        null,
        new TypeReference<TokenResponse>() {}
    );

    return response.getData().getToken();
  }

  @Getter @Setter
  private static class TokenResponse {

    @JsonProperty("access_token")
    private String token;
  }
}
