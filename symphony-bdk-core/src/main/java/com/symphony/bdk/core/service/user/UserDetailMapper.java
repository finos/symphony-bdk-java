package com.symphony.bdk.core.service.user;

import com.symphony.bdk.gen.api.model.UserAttributes;
import com.symphony.bdk.gen.api.model.UserDetail;
import com.symphony.bdk.gen.api.model.V2UserAttributes;
import com.symphony.bdk.gen.api.model.V2UserDetail;

class UserDetailMapper {

  protected static V2UserAttributes.AccountTypeEnum toV2AccountType(UserAttributes.AccountTypeEnum accountTypeEnum) {
    if (accountTypeEnum != null) {
      switch (accountTypeEnum) {
        case SYSTEM:
          return V2UserAttributes.AccountTypeEnum.SYSTEM;
        case NORMAL:
          return V2UserAttributes.AccountTypeEnum.NORMAL;
      }
    }
    return null;
  }

  protected static V2UserAttributes toV2UserAttribute(UserAttributes userAttributes) {
    if (userAttributes != null) {
      return new V2UserAttributes()
          .emailAddress(userAttributes.getEmailAddress())
          .firstName(userAttributes.getFirstName())
          .lastName(userAttributes.getLastName())
          .userName(userAttributes.getUserName())
          .displayName(userAttributes.getDisplayName())
          .companyName(userAttributes.getCompanyName())
          .department(userAttributes.getDepartment())
          .division(userAttributes.getDivision())
          .title(userAttributes.getTitle())
          .workPhoneNumber(userAttributes.getWorkPhoneNumber())
          .mobilePhoneNumber(userAttributes.getMobilePhoneNumber())
          .smsNumber(userAttributes.getSmsNumber())
          .accountType(toV2AccountType(userAttributes.getAccountType()))
          .location(userAttributes.getLocation())
          .jobFunction(userAttributes.getJobFunction())
          .assetClasses(userAttributes.getAssetClasses())
          .industries(userAttributes.getIndustries());
    }
    return null;
  }

  public static V2UserDetail toV2UserDetail(UserDetail userDetail) {
    if (userDetail != null) {
      return new V2UserDetail()
          .userAttributes(toV2UserAttribute(userDetail.getUserAttributes()))
          .userSystemInfo(userDetail.getUserSystemInfo())
          .features(userDetail.getFeatures())
          .apps(userDetail.getApps())
          .groups(userDetail.getGroups())
          .roles(userDetail.getRoles())
          .disclaimers(userDetail.getDisclaimers())
          .avatar(userDetail.getAvatar());
    }
    return null;
  }
}
