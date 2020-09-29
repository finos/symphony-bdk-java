package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.ExtensionAppTokensRepository;

import org.apiguardian.api.API;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@API(status = API.Status.STABLE)
public class InMemoryTokensRepository implements ExtensionAppTokensRepository {

  private final Map<String, String> tokens;

  public InMemoryTokensRepository() {
    tokens = new ConcurrentHashMap<>();
  }

  @Override
  public void save(String appToken, String symphonyToken) {
    tokens.put(appToken, symphonyToken);
  }

  @Override
  public Optional<String> get(String appToken) {
    return Optional.ofNullable(tokens.get(appToken));
  }
}
