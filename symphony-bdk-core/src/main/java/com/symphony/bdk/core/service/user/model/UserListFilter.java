package com.symphony.bdk.core.service.user.model;

import com.symphony.bdk.core.service.user.constant.RoleId;
import com.symphony.bdk.core.service.user.constant.UserFeature;

import lombok.Getter;
import lombok.ToString;
import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Filter to be used in {@link com.symphony.bdk.core.service.user.UserService#listUsers} and
 * {@link com.symphony.bdk.core.service.user.UserService#listAllUsers}. Unset criteria are ignored, returned users
 * match all the set ones.
 */
@Getter
@ToString
@API(status = API.Status.EXPERIMENTAL)
public class UserListFilter {

  private @Nullable Long userId;
  private @Nullable Boolean active;
  private @Nullable List<String> roles;
  private @Nullable List<String> enabledEntitlements;
  private @Nullable List<String> disabledEntitlements;
  private @Nullable Instant createdDateFrom;
  private @Nullable Instant createdDateTo;

  /**
   * @param userId only return the user with this id.
   * @return this filter
   */
  public UserListFilter userId(@Nullable Long userId) {
    this.userId = userId;
    return this;
  }

  /**
   * @param active if true only return active users, if false only inactive ones.
   * @return this filter
   */
  public UserListFilter active(@Nullable Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Replaces the roles set by {@link #roleNames(List)}.
   *
   * @param roles only return users having at least one of these roles.
   * @return this filter
   */
  public UserListFilter roles(@Nullable List<RoleId> roles) {
    this.roles = names(roles);
    return this;
  }

  /**
   * Same as {@link #roles(List)}, for roles not listed in {@link RoleId}. Replaces the roles set by
   * {@link #roles(List)}.
   * <p>
   * Role ids are matched exactly, an unknown one matches no user rather than being rejected.
   *
   * @param roleNames ids of the roles, e.g. INDIVIDUAL.
   * @return this filter
   */
  public UserListFilter roleNames(@Nullable List<String> roleNames) {
    this.roles = withoutNulls(roleNames);
    return this;
  }

  /**
   * Replaces the entitlements set by {@link #enabledEntitlementNames(List)}.
   *
   * @param enabledEntitlements only return users having all of these entitlements enabled.
   * @return this filter
   */
  public UserListFilter enabledEntitlements(@Nullable List<UserFeature> enabledEntitlements) {
    this.enabledEntitlements = names(enabledEntitlements);
    return this;
  }

  /**
   * Same as {@link #enabledEntitlements(List)}, for entitlements not listed in {@link UserFeature}. Replaces the
   * entitlements set by {@link #enabledEntitlements(List)}. Names are not validated, an unknown one is never enabled, so
   * no user is returned.
   *
   * @param enabledEntitlementNames names of the entitlements, e.g. canCreatePublicRoom.
   * @return this filter
   */
  public UserListFilter enabledEntitlementNames(@Nullable List<String> enabledEntitlementNames) {
    this.enabledEntitlements = withoutNulls(enabledEntitlementNames);
    return this;
  }

  /**
   * Replaces the entitlements set by {@link #disabledEntitlementNames(List)}.
   *
   * @param disabledEntitlements only return users having all of these entitlements disabled.
   * @return this filter
   */
  public UserListFilter disabledEntitlements(@Nullable List<UserFeature> disabledEntitlements) {
    this.disabledEntitlements = names(disabledEntitlements);
    return this;
  }

  /**
   * Same as {@link #disabledEntitlements(List)}, for entitlements not listed in {@link UserFeature}. Replaces the
   * entitlements set by {@link #disabledEntitlements(List)}. Names are not validated, an unknown one is never enabled, so
   * it excludes no user.
   *
   * @param disabledEntitlementNames names of the entitlements, e.g. canCreatePublicRoom.
   * @return this filter
   */
  public UserListFilter disabledEntitlementNames(@Nullable List<String> disabledEntitlementNames) {
    this.disabledEntitlements = withoutNulls(disabledEntitlementNames);
    return this;
  }

  /**
   * Creation times are only precise to the second.
   *
   * @param createdDateFrom only return users created at or after this time.
   * @return this filter
   */
  public UserListFilter createdDateFrom(@Nullable Instant createdDateFrom) {
    this.createdDateFrom = createdDateFrom;
    return this;
  }

  /**
   * Creation times are only precise to the second. The call is rejected with a 400 if it is before
   * {@link #createdDateFrom(Instant)}.
   *
   * @param createdDateTo only return users created before this time.
   * @return this filter
   */
  public UserListFilter createdDateTo(@Nullable Instant createdDateTo) {
    this.createdDateTo = createdDateTo;
    return this;
  }

  private static <E extends Enum<E>> @Nullable List<String> names(@Nullable List<E> values) {
    return values == null ? null : values.stream()
        .filter(Objects::nonNull)
        .map(Enum::name)
        .collect(Collectors.toList());
  }

  private static @Nullable List<String> withoutNulls(@Nullable List<String> values) {
    return values == null ? null : values.stream().filter(Objects::nonNull).collect(Collectors.toList());
  }
}
