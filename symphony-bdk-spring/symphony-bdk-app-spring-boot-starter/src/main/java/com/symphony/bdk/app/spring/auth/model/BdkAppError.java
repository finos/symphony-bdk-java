package com.symphony.bdk.app.spring.auth.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BdkAppError {

  private int status;
  private BdkAppErrorCode code;
  private List<String> message;
}
