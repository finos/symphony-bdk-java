package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.util.UserIDUtil;
import com.symphony.bdk.ext.group.SymphonyGroupBdkExtension;
import com.symphony.bdk.ext.group.SymphonyGroupService;
import com.symphony.bdk.gen.api.model.BaseProfile;
import com.symphony.bdk.gen.api.model.BaseType;
import com.symphony.bdk.gen.api.model.CreateGroup;
import com.symphony.bdk.gen.api.model.GroupList;
import com.symphony.bdk.gen.api.model.Member;
import com.symphony.bdk.gen.api.model.Owner;
import com.symphony.bdk.gen.api.model.ReadGroup;
import com.symphony.bdk.gen.api.model.Status;
import com.symphony.bdk.gen.api.model.TypeList;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
@SuppressWarnings("all")
public class GroupExtensionExample {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = SymphonyBdk.builder()
        .config(loadFromSymphonyDir("config.yaml"))
        .extension(SymphonyGroupBdkExtension.class) // load extension
        .build();

    // extension is also service provider
    final SymphonyGroupService groupService = bdk.extensions().service(SymphonyGroupBdkExtension.class);

    // list active types
    TypeList activeTypes = groupService.listTypes(Status.ACTIVE, null, null, null, null);
    for (BaseType type : activeTypes.getData()) {
      log.info("{}", groupService.getType(type.getId()));

      // list groups by type
      GroupList groups = groupService.listGroups(type.getId(), Status.ACTIVE, null, null, null, null);
      for (ReadGroup group : groups.getData()) {
        log.info("{}", groupService.getGroup(group.getId()));
      }
    }

    // list deleted types
    TypeList deletedTypes = groupService.listTypes(Status.DELETED, null, null, null, null);
    for (BaseType type : deletedTypes.getData()) {
      log.info("{}", groupService.getType(type.getId()));
    }
  }

  private static ReadGroup createGroup(SymphonyGroupService groupService, SymphonyBdk bdk) {

    final String name = "Group - Tibot";
    final long userId = bdk.botInfo().getId();
    final int tenantId = UserIDUtil.extractTenantId(userId);

    final CreateGroup group = new CreateGroup();
    group.setType("SDL");
    group.setName(name);
    group.setOwnerType(Owner.TENANT);
    group.setOwnerId((long) tenantId);
    group.setMembers(Collections.singletonList(new Member().memberId(userId).memberTenant(tenantId)));
    group.setProfile(new BaseProfile().displayName(name));

    return groupService.insertGroup(group);
  }
}
