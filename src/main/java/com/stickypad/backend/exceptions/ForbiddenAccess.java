package com.stickypad.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenAccess extends RuntimeException {

  public ForbiddenAccess(String message) {
    super(message);
  }

}
