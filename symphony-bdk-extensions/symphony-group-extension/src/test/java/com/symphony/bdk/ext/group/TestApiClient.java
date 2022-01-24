package com.symphony.bdk.ext.group;

import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.Pair;

import java.util.Collections;
import java.util.List;

public abstract class TestApiClient implements ApiClient {

  @Override
  public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
    return Collections.singletonList(new Pair(name, value.toString()));
  }

  @Override
  public String escapeString(String str) {
    return str;
  }
}
