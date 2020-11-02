package com.symphony.bdk.app.spring.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global error handler for the runtime exception thrown by the RestController.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BdkAppException.class)
  public ResponseEntity<Object> handleBdkAppException(final BdkAppException e, final WebRequest request) {
    final BdkAppError error = BdkAppError.fromException(e);
    return super.handleExceptionInternal(e, error, new HttpHeaders(), e.getErrorCode().getStatus(), request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request
  ) {

    final BdkAppError error = new BdkAppError();
    error.setCode(BdkAppErrorCode.MISSING_FIELDS);

    final List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());

    error.setMessage(errors);
    return super.handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
  }
}
