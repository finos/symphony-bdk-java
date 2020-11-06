package com.symphony.bdk.core.service.pagination.model;

import lombok.Getter;
import org.apiguardian.api.API;

import javax.annotation.Nonnull;

/**
 * Pagination Attribute model to be used in default pagination methods provided by bdk services class.
 */
@Getter
@API(status = API.Status.STABLE)
public class RangePaginationAttribute {

  /**
   * The rank of the last item wished in the payload
   * We want to show the maximum number of items positioned right before and including this one.
   */
  private final Integer before;

  /**
   * The rank of the first item wished from the payload + 1
   * We want to show the maximum number of items positioned right after and excluding this one.
   */
  private final Integer after;

  /**
   * Maximum number of records to be returned.
   */
  private final Integer limit;

  public RangePaginationAttribute(@Nonnull Integer before, @Nonnull Integer after, @Nonnull Integer limit) {
    this.before = before;
    this.after = after;
    this.limit = limit;
  }
}
