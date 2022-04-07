package com.doodle.backendchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "conflict state of the target resource")
public class ConflictException extends RuntimeException {

  public ConflictException(String message) {
    super(message);
  }
}
