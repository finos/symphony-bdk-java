package com.symphony.bdk.http.api.auth;

import com.symphony.bdk.http.api.ApiClient;
import org.apiguardian.api.API;

import java.util.Map;

/**
 * Definition of an <a href="https://swagger.io/docs/specification/authentication">authentication scheme</a>.
 * <p>
 * An authentication scheme can be set through the {@link ApiClient#getAuthentications()} map.
 */
@API(status = API.Status.STABLE)
public interface Authentication {

  /**
   * Apply authentication settings to header params.
   *
   * @param requestHeaders Map of header parameters
   */
  void apply(Map<String, String> requestHeaders);
}
