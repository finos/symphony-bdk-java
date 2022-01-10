package com.symphony.bdk.examples.spring.group;

import com.symphony.bdk.ext.group.SymphonyGroupService;
import com.symphony.bdk.ext.group.gen.api.model.Status;
import com.symphony.bdk.ext.group.gen.api.model.TypeList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupApi {

  private final SymphonyGroupService groupService;

  @GetMapping("/types")
  public TypeList getTypes() {
    return this.groupService.listTypes(Status.ACTIVE, null, null, null, null);
  }
}
