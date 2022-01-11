package com.symphony.bdk.core.client;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.ApiResponse;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.auth.Authentication;
import com.symphony.bdk.http.api.auth.OAuth;
import com.symphony.bdk.http.api.util.TypeReference;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * An {@link ApiClient} implementation used when common jwt feature is enabled.
 * It contains an {@link ApiClient} that will allow the usage of the Authentication header in each API call.
 * It will also remove the sessionToken header from the request.
 * Before adding the jwt as Authorization header the client is checking the jwt expiration, if the jwt is expired
 * re-authentication will be triggered and the jwt will be refreshed.
 */

@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class BearerEnabledApiClient implements ApiClient {
  private final ApiClient apiClient;
  private final AuthSession authSession;
  private final boolean isCommonJwtEnabled;

  public BearerEnabledApiClient(ApiClient apiClient, AuthSession authSession, boolean isCommonJwtEnabled) {
    this.apiClient = apiClient;
    this.authSession = authSession;
    this.isCommonJwtEnabled = isCommonJwtEnabled;
  }

  @Override
  public <T> ApiResponse<T> invokeAPI(String path, String method, List<Pair> queryParams, Object body,
      Map<String, String> headerParams, Map<String, String> cookieParams, Map<String, Object> formParams, String accept,
      String contentType, String[] authNames, TypeReference<T> returnType) throws ApiException {

    final OAuth auth = new OAuth();
    auth.setCommonJwtEnabled(isCommonJwtEnabled);
    auth.setBearerToken(retrieveBearerToken());
    apiClient.getAuthentications().put("bearerAuth", auth);

    return apiClient.invokeAPI(path, method, queryParams, body, headerParams, cookieParams, formParams, accept,
        contentType, authNames, returnType);
  }

  private String retrieveBearerToken() throws ApiException {
    if (isCommonJwtEnabled) {
      try {
        refreshTokenIfNeeded();
        if (authSession.getAuthorizationToken() != null) {
          return authSession.getAuthorizationToken();
        }
      } catch (AuthInitializationException | AuthUnauthorizedException e) {
        throw new ApiException(e.getMessage(), e);
      }
    }
    return null;
  }

  @Override
  public String getBasePath() {
    return apiClient.getBasePath();
  }

  @Override
  public String parameterToString(Object param) {
    return apiClient.parameterToString(param);
  }

  @Override
  public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
    return apiClient.parameterToPairs(collectionFormat, name, value);
  }

  @Override
  public String selectHeaderAccept(String... accepts) {
    return apiClient.selectHeaderAccept(accepts);
  }

  @Override
  public String selectHeaderContentType(String... contentTypes) {
    return apiClient.selectHeaderContentType(contentTypes);
  }

  @Override
  public String escapeString(String str) {
    return apiClient.escapeString(str);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Authentication> getAuthentications() {
    return this.apiClient.getAuthentications();
  }

  /**
   * Trigger jwt refresh if expired
   */
  private void refreshTokenIfNeeded() throws AuthInitializationException, AuthUnauthorizedException {
    int interval = 5;
    if (authSession.getAuthorizationToken() == null || authSession.getAuthTokenExpirationDate() == null) {
      log.debug("Common jwt feature is not available in your pod, version should be greater than 20.13. "
          + "Switching back to the session token authentication.");
      return;
    }

    if (Instant.now().getEpochSecond() + interval >= authSession.getAuthTokenExpirationDate()) {
      authSession.refreshAuthToken();
    }
  }
}
