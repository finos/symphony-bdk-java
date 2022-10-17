package com.symphony.bdk.examples.spring.api;

import com.symphony.bdk.ext.group.SymphonyGroupService;
import com.symphony.bdk.ext.group.gen.api.model.GroupList;
import com.symphony.bdk.ext.group.gen.api.model.Status;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/groups")
public class GroupApi {

  private final SymphonyGroupService groupService;

  @Autowired
  public GroupApi(SymphonyGroupService groupService) {
    this.groupService = groupService;
  }

  @GetMapping
  public GroupList getGroups() {
    return this.groupService.listGroups(Status.ACTIVE, null, null, null, null);
  }
}
