package com.symphony.bot.sdk.internal.symphony.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import model.UserInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Symphony user data
 *
 * @author Gabriel Berberian
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SymphonyUser {

  private Long userId;
  private String emailAddress;
  private String firstName;
  private String lastName;
  private String displayName;
  private String title;
  private String company;
  private String username;
  private String location;
  private String workPhoneNumber;
  private String mobilePhoneNumber;
  private String jobFunction;
  private String department;
  private String division;
  private List<UserAvatar> avatars;

  public SymphonyUser(UserInfo userInfo) {
    this.userId = userInfo.getId();
    this.emailAddress = userInfo.getEmailAddress();
    this.firstName = userInfo.getFirstName();
    this.lastName = userInfo.getLastName();
    this.displayName = userInfo.getDisplayName();
    this.title = userInfo.getTitle();
    this.company = userInfo.getCompany();
    this.username = userInfo.getUsername();
    this.location = userInfo.getLocation();
    this.workPhoneNumber = userInfo.getWorkPhoneNumber();
    this.mobilePhoneNumber = userInfo.getMobilePhoneNumber();
    this.jobFunction = userInfo.getJobFunction();
    this.department = userInfo.getDepartment();
    this.division = userInfo.getDivision();
    this.avatars = userInfo.getAvatars().stream().map(UserAvatar::new).collect(Collectors.toList());
  }

}
