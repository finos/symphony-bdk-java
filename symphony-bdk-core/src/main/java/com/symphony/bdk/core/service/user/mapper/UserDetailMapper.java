package com.symphony.bdk.core.service.user.mapper;

import com.symphony.bdk.gen.api.model.UserDetail;
import com.symphony.bdk.gen.api.model.V2UserDetail;

import org.apiguardian.api.API;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
@API(status = API.Status.INTERNAL)
public interface UserDetailMapper {

  UserDetailMapper INSTANCE = Mappers.getMapper(UserDetailMapper.class);

  @Mapping(target = "userAttributes.twoFactorAuthPhone", ignore = true)
  @Mapping(target = "userAttributes.recommendedLanguage", ignore = true)
  @Mapping(target = "userAttributes.marketCoverage", ignore = true)
  @Mapping(target = "userAttributes.responsibility", ignore = true)
  @Mapping(target = "userAttributes.function", ignore = true)
  @Mapping(target = "userAttributes.instrument", ignore = true)
  @Mapping(target = "userAttributes.currentKey", ignore = true)
  @Mapping(target = "userAttributes.previousKey", ignore = true)
  V2UserDetail userDetailToV2UserDetail(UserDetail userDetail);
}
