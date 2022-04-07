package com.doodle.backendchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED)
public class PreconditionFailedException extends RuntimeException {
  public PreconditionFailedException(String message) {
    super(message);
  }
}
