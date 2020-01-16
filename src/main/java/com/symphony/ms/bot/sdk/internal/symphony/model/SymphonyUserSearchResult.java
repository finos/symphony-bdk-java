package com.symphony.ms.bot.sdk.internal.symphony.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.UserSearchResult;

/**
 * Symphony user search result
 *
 * @author Gabriel Berberian
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SymphonyUserSearchResult {

  private int count;
  private int skip;
  private String query;
  private Map<String, String> filters;
  private List<SymphonyUser> users;
  private int limit;

  public SymphonyUserSearchResult(UserSearchResult searchUsers) {
    this.count = searchUsers.getCount();
    this.skip = searchUsers.getSkip();
    this.query = searchUsers.getQuery();
    this.filters = searchUsers.getFilters();
    this.users = searchUsers.getUsers() != null ? searchUsers.getUsers()
        .stream()
        .map(SymphonyUser::new)
        .collect(Collectors.toList()) : Collections.emptyList();
    this.limit = searchUsers.getLimit();
  }

}
