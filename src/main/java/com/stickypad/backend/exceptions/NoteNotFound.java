package com.stickypad.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoteNotFound extends RuntimeException {

  public NoteNotFound(String message) {
    super(message);
  }

}
