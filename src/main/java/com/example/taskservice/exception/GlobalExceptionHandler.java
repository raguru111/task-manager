
package com.example.taskservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<ErrorResponse> handleValidation(
  MethodArgumentNotValidException ex,
  HttpServletRequest request
 ) {
  Map<String, String> errors = new HashMap<>();
  ex.getBindingResult().getFieldErrors()
   .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
  return ResponseEntity.status(HttpStatus.BAD_REQUEST)
   .body(error(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), errors));
 }

 @ExceptionHandler(BindException.class)
 public ResponseEntity<ErrorResponse> handleBind(
  BindException ex,
  HttpServletRequest request
 ) {
  Map<String, String> errors = new HashMap<>();
  ex.getBindingResult().getFieldErrors()
   .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
  return ResponseEntity.status(HttpStatus.BAD_REQUEST)
   .body(error(HttpStatus.BAD_REQUEST, "Binding failed", request.getRequestURI(), errors));
 }

 @ExceptionHandler({
  HttpMessageNotReadableException.class,
  MethodArgumentTypeMismatchException.class
 })
 public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
  return ResponseEntity.status(HttpStatus.BAD_REQUEST)
   .body(error(HttpStatus.BAD_REQUEST, "Bad request", request.getRequestURI(), null));
 }

 @ExceptionHandler(NoHandlerFoundException.class)
 public ResponseEntity<ErrorResponse> handleNotFound(
  NoHandlerFoundException ex,
  HttpServletRequest request
 ) {
  return ResponseEntity.status(HttpStatus.NOT_FOUND)
   .body(error(HttpStatus.NOT_FOUND, "Not found", request.getRequestURI(), null));
 }

 @ExceptionHandler(ResponseStatusException.class)
 public ResponseEntity<ErrorResponse> handleResponseStatus(
  ResponseStatusException ex,
  HttpServletRequest request
 ) {
  HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
  return ResponseEntity.status(status)
   .body(error(status, ex.getReason() != null ? ex.getReason() : status.getReasonPhrase(),
    request.getRequestURI(), null));
 }

 @ExceptionHandler(Exception.class)
 public ResponseEntity<ErrorResponse> handle(Exception ex, HttpServletRequest request) {
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
   .body(error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request.getRequestURI(), null));
 }

 private static ErrorResponse error(
  HttpStatus status,
  String message,
  String path,
  Map<String, String> details
 ) {
  return new ErrorResponse(
   OffsetDateTime.now().toString(),
   status.value(),
   message,
   path,
   details
  );
 }

 public static class ErrorResponse {
  private final String timestamp;
  private final int status;
  private final String message;
  private final String path;
  private final Map<String, String> details;

  public ErrorResponse(
   String timestamp,
   int status,
   String message,
   String path,
   Map<String, String> details
  ) {
   this.timestamp = timestamp;
   this.status = status;
   this.message = message;
   this.path = path;
   this.details = details;
  }

  public String getTimestamp() {
   return timestamp;
  }

  public int getStatus() {
   return status;
  }

  public String getMessage() {
   return message;
  }

  public String getPath() {
   return path;
  }

  public Map<String, String> getDetails() {
   return details;
  }
 }
}
