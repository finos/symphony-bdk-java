package com.symphony.bdk.core.service.user;

import com.symphony.bdk.gen.api.model.UserDetail;
import com.symphony.bdk.gen.api.model.V2UserDetail;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserDetailMapper {

  UserDetailMapper INSTANCE = Mappers.getMapper(UserDetailMapper.class);

  V2UserDetail userDetailToV2UserDetail(UserDetail userDetail);
}
