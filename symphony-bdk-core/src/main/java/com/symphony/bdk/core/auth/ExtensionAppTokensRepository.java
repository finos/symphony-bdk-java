package com.symphony.bdk.core.auth;

import java.util.Optional;

/**
 * Repository to store (appToken, symphonyToken) entries corresponding to extensionApp authentications.
 */
public interface ExtensionAppTokensRepository {
  /**
   * Saves an extensionApp authentication
   *
   * @param appToken the appToken used to authenticate
   * @param symphonyToken the returned symphonyToken to save
   */
  void save(String appToken, String symphonyToken);

  /**
   * Get the optional symphony token associated to a given appToken
   * @param appToken the appToken used in a previous authentication
   * @return an optional containing the symphonyToken if it exists, an empty optional otherwise
   */
  Optional<String> get(String appToken);
}
