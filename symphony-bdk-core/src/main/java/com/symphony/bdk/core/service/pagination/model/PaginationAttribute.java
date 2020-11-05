package com.symphony.bdk.core.service.pagination.model;

import lombok.Getter;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Pagination Attribute model to be used in default pagination methods provided by bdk services class.
 */
@Getter
@API(status = API.Status.STABLE)
public class PaginationAttribute {

  /**
   * Number of records to skip.
   */
  private final Integer skip;

  /**
   * Maximum number of records to be returned.
   */
  private final Integer limit;

  public PaginationAttribute(@Nonnull Integer skip, @Nonnull Integer limit) {
    this.skip = skip;
    this.limit = limit;
  }
}
