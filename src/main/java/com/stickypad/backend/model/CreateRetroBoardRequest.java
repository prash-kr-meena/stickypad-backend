package com.stickypad.backend.model;

import jakarta.validation.constraints.NotBlank;

public record CreateRetroBoardRequest(
  @NotBlank(message = "Host UserId is required!")
  String hostUserId
) { }
