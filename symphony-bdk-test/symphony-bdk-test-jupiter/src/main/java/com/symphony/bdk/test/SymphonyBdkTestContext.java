package com.symphony.bdk.test;

import com.symphony.bdk.gen.api.model.UserV2;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SymphonyBdkTestContext {
  private final UserV2 botInfo;
}
