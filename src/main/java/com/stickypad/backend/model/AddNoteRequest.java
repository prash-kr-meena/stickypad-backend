package com.stickypad.backend.model;

import jakarta.validation.constraints.NotBlank;

public record AddNoteRequest(
  @NotBlank String userId,
  @NotBlank String content
) {
}
