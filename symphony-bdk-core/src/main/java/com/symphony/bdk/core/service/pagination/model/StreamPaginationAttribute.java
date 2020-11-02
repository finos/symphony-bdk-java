package com.symphony.bdk.core.service.pagination.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Stream Pagination Attribute model to be used in stream pagination methods provided by bdk services class.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
public class StreamPaginationAttribute {

  private Integer chunkSize;
  private Integer totalSize;
}
