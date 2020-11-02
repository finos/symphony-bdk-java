package com.symphony.bdk.core.service.pagination.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Pagination Attribute model to be used in default pagination methods provided by bdk services class.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
public class PaginationAttribute {

  private Integer skip;
  private Integer limit;
}
