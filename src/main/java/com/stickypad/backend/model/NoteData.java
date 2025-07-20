package com.stickypad.backend.model;

import jakarta.validation.constraints.NotBlank;

public record NoteData(
  @NotBlank String userId,
  @NotBlank String content
) {
}
