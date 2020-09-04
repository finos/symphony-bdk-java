package com.symphony.bdk.examples;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.gen.api.model.UserFilter;
import com.symphony.bdk.gen.api.model.V2UserDetail;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UserExampleMain {

  public static void main(String[] args)
      throws BdkConfigException, AuthUnauthorizedException, AuthInitializationException {

    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config.yaml");

    SymphonyBdk bdk = new SymphonyBdk(config);
    UserFilter filter = new UserFilter().status(UserFilter.StatusEnum.DISABLED).feature("canCreatePublicRoom");
    log.info("Get user by filter");
    List<V2UserDetail> userDetailList = bdk.users().listUsersDetail(filter);
    log.info("Retrieve {} records", userDetailList.size());
    log.info("First record: ");
    log.info(userDetailList.get(0).getUserAttributes().getUserName());
    log.info("Account type: {}", userDetailList.get(0).getUserAttributes().getAccountType());
    log.info("Company: {}", userDetailList.get(0).getUserAttributes().getCompanyName());
    log.info("Role: {}", userDetailList.get(0).getRoles());
  }
}
