package com.symphony.bdk.groups;

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
import lombok.Setter;
import org.apiguardian.api.API;

// FIXME to be removed once https://perzoinc.atlassian.net/browse/PLAT-11564 done
@API(status = API.Status.DEPRECATED)
public class OAuthHelper {

  public static String retrieveBearerToken(ApiClient loginClient, AuthSession session) throws ApiException {

    final ApiResponse<TokenResponse> response = loginClient.invokeAPI(
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
