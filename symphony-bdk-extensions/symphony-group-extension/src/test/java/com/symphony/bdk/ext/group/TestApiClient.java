package com.symphony.bdk.ext.group;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.Pair;
import com.symphony.bdk.http.api.auth.Authentication;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TestApiClient implements ApiClient {

  private Map<String, Authentication> authentications = new HashMap<>();

  @Override
  public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
    return Collections.singletonList(new Pair(name, value.toString()));
  }

  @Override
  public String escapeString(String str) {
    return str;
  }

  @Override
  public Map<String, Authentication> getAuthentications() {
    return authentications;
  }

  @Override
  public String getBasePath() {
    return "";
  }
}
