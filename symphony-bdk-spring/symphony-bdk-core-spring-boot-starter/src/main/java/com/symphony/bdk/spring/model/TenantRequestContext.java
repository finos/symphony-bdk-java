package com.symphony.bdk.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequestContext {
  private String tenantId;
  private String tenantHost;

}
