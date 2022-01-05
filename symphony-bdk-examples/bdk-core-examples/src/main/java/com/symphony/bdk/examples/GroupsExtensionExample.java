package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.gen.api.model.BaseProfile;
import com.symphony.bdk.gen.api.model.CreateGroup;
import com.symphony.bdk.gen.api.model.Member;
import com.symphony.bdk.gen.api.model.Owner;
import com.symphony.bdk.gen.api.model.ReadGroup;
import com.symphony.bdk.groups.SymphonyGroupsBdkExtension;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class GroupsExtensionExample {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    final SymphonyGroupsBdkExtension groups = bdk.getExtension(SymphonyGroupsBdkExtension.class);

    String name = "SDL - Tibot - 01";

    final CreateGroup group = new CreateGroup();
    group.setType("SDL");
    group.setName(name);
    group.setOwnerType(Owner.TENANT);
    group.setOwnerId(189L);
    group.setMembers(Collections.singletonList(new Member().memberId(bdk.botInfo().getId()).memberTenant(189)));
    group.setProfile(new BaseProfile().displayName(name));

    ReadGroup response = groups.insertGroup(group);

    log.info("Group successfully created -> {}", response);
  }
}
