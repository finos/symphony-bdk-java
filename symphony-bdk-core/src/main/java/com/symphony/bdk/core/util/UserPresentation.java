package com.symphony.bdk.core.util;

import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import org.symphonyoss.symphony.messageml.util.IUserPresentation;

/**
 * Implementation of {@link IUserPresentation} using by {@link DataProvider}.
 */
@AllArgsConstructor
@API(status = API.Status.INTERNAL)
public class UserPresentation implements IUserPresentation {

  private final Long uid;
  private final String screenName;
  private final String prettyName;
  private final String email;

  @Override
  public long getId() {
    return this.uid;
  }

  @Override
  public String getScreenName() {
    return this.screenName;
  }

  @Override
  public String getPrettyName() {
    return this.prettyName;
  }

  @Override
  public String getEmail() {
    return this.email;
  }
}
