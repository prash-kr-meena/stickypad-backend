package com.stickypad.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_MODIFIED)
public class NoteUnChanged extends RuntimeException {

  public NoteUnChanged(String message) {
    super(message);
  }

}
