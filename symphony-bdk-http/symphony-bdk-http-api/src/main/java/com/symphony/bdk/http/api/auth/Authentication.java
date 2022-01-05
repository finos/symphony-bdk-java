package com.symphony.bdk.http.api.auth;

import com.symphony.bdk.http.api.Pair;

import org.apiguardian.api.API;

import java.util.List;
import java.util.Map;

@API(status = API.Status.STABLE)
public interface Authentication {

  /**
   * Apply authentication settings to header and query params.
   *
   * @param queryParams List of query parameters
   * @param headerParams Map of header parameters
   */
  void applyToParams(List<Pair> queryParams, Map<String, String> headerParams);
}
