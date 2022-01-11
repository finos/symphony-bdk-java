package com.symphony.bdk.examples.spring.api;

import com.symphony.bdk.ext.group.SymphonyGroupService;
import com.symphony.bdk.ext.group.gen.api.model.GroupList;
import com.symphony.bdk.ext.group.gen.api.model.Status;
import com.symphony.bdk.ext.group.gen.api.model.TypeList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupApi {

  private final SymphonyGroupService groupService;

  @GetMapping
  public GroupList getGroups(@RequestParam(defaultValue = "SDL") String type) {
    return this.groupService.listGroups(type, Status.ACTIVE, null, null, null, null);
  }

  @GetMapping("/types")
  public TypeList getTypes() {
    return this.groupService.listTypes(Status.ACTIVE, null, null, null, null);
  }
}
