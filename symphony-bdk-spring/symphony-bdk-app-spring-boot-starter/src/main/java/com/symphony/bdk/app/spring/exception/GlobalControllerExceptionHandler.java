package com.symphony.bdk.app.spring.exception;

import com.symphony.bdk.app.spring.auth.model.BdkAppError;
import com.symphony.bdk.app.spring.auth.model.BdkAppErrorCode;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global error handler for the runtime exception thrown by the RestController.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({AppAuthException.class})
  public ResponseEntity<Object> handleUnauthorizedException(Exception e, WebRequest request) {
    BdkAppError error = new BdkAppError();
    error.setStatus(HttpStatus.UNAUTHORIZED.value());
    error.setCode(BdkAppErrorCode.UNAUTHORIZED);
    error.setMessage(Collections.singletonList(e.getMessage()));

    return handleExceptionInternal(e, error, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler({InvalidTokenException.class})
  public ResponseEntity<Object> handleInvalidTokenException(Exception e, WebRequest request) {
    BdkAppError error = new BdkAppError();
    error.setStatus(HttpStatus.UNAUTHORIZED.value());
    error.setCode(BdkAppErrorCode.INVALID_TOKEN);
    error.setMessage(Collections.singletonList(e.getMessage()));

    return handleExceptionInternal(e, error, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler({InvalidJwtException.class})
  public ResponseEntity<Object> handleInvalidJwtException(Exception e, WebRequest request) {
    BdkAppError error = new BdkAppError();
    error.setStatus(HttpStatus.UNAUTHORIZED.value());
    error.setCode(BdkAppErrorCode.INVALID_JWT);
    error.setMessage(Collections.singletonList(e.getMessage()));

    return handleExceptionInternal(e, error, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    BdkAppError error = new BdkAppError();
    error.setStatus(status.value());
    error.setCode(BdkAppErrorCode.MISSING_FIELDS);

    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());

    error.setMessage(errors);
    return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
  }
}
