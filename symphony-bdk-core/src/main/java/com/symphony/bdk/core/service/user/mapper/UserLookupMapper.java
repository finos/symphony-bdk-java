package com.symphony.bdk.core.service.user.mapper;

import com.symphony.bdk.gen.api.model.UserLookup;
import com.symphony.bdk.gen.api.model.UserV2;

import org.apiguardian.api.API;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
@API(status = API.Status.INTERNAL)
public interface UserLookupMapper {

  UserLookupMapper INSTANCE = Mappers.getMapper(UserLookupMapper.class);

  UserV2 userLookupToUserV2(UserLookup userLookup);
}
