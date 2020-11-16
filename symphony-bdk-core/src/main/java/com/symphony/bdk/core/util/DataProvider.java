package com.symphony.bdk.core.util;

import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.model.UserV2;

import com.google.common.collect.ImmutableSet;
import org.apiguardian.api.API;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.util.IDataProvider;
import org.symphonyoss.symphony.messageml.util.IUserPresentation;

import java.net.URI;
import java.util.Set;

/**
 * Implementation of {@link IDataProvider} being used by {@link MessageMLValidator}.
 */
@API(status = API.Status.INTERNAL)
public class DataProvider implements IDataProvider {

  private static final Set<String> STANDARD_URI_SCHEMES = ImmutableSet.of("http", "https");
  private final UserService userService;

  public DataProvider(UserService userService) {
    this.userService = userService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IUserPresentation getUserPresentation(String email) throws InvalidInputException {
    UserV2 user = this.userService.getUser(null, email, null, true);
    if (user == null) {
      user = this.userService.getUser(null, email, null, false);
    }
    if (user == null) {
      throw new InvalidInputException("Failed to lookup user \"" + email + "\"");
    }

    return new UserPresentation(user.getId(), user.getDisplayName(), user.getDisplayName(), user.getEmailAddress());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IUserPresentation getUserPresentation(Long uid) throws InvalidInputException {
    UserV2 user = this.userService.getUser(uid, null, null, true);
    if (user == null) {
      user = this.userService.getUser(uid, null, null, false);
    }
    if (user == null) {
      throw new InvalidInputException("Failed to lookup user \"" + uid + "\"");
    }

    return new UserPresentation(user.getId(), user.getDisplayName(), user.getDisplayName(), user.getEmailAddress());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validateURI(URI uri) throws InvalidInputException {
    if (!STANDARD_URI_SCHEMES.contains(uri.getScheme().toLowerCase())) {
      throw new InvalidInputException(
          "URI scheme \"" + uri.getScheme() + "\" is not supported by the pod.");
    }
  }
}
