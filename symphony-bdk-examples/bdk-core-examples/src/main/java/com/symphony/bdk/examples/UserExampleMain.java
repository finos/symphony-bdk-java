package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.user.constant.UserFeature;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.StreamType;
import com.symphony.bdk.gen.api.model.UserFilter;
import com.symphony.bdk.gen.api.model.V2UserDetail;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UserExampleMain {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    final UserFilter filter = new UserFilter()
        .status(UserFilter.StatusEnum.DISABLED)
        .feature(UserFeature.canCreatePublicRoom.name());

    log.info("Get user by filter");
    final List<V2UserDetail> userDetailList = bdk.users().listUsersDetail(filter);

    log.info("Retrieve {} records", userDetailList.size());
    log.info("First record: ");
    log.info(userDetailList.get(0).getUserAttributes().getUserName());
    log.info("Account type: {}", userDetailList.get(0).getUserAttributes().getAccountType());
    log.info("Company: {}", userDetailList.get(0).getUserAttributes().getCompanyName());
    log.info("Role: {}", userDetailList.get(0).getRoles());

    AuthSession oboSession = bdk.obo("hong.le");
    StreamFilter streamFilter = new StreamFilter().addStreamTypesItem(new StreamType().type(StreamType.TypeEnum.IM));
    List<StreamAttributes> streamsList = bdk.streams().listStreams(oboSession, streamFilter);
    log.info("Streams List: {}", streamsList);
  }
}
