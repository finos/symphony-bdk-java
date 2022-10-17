package com.symphony.bdk.examples.spring.api;

import com.symphony.bdk.http.api.ApiRuntimeException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

  private final ObjectMapper objectMapper;

  @Data
  static class ErrorMessage {
    private String code;
    private String message;
  }

  @ExceptionHandler(value = ApiRuntimeException.class)
  public ResponseEntity<ErrorMessage> resourceNotFoundException(final ApiRuntimeException ex) throws JsonProcessingException {
    return new ResponseEntity<>(this.objectMapper.readValue(ex.getResponseBody(), ErrorMessage.class), HttpStatus.valueOf(ex.getCode()));
  }
}
