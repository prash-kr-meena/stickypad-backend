package com.stickypad.backend.model;

import lombok.Builder;

@Builder
public record Note(
  String noteId,
  String userId,
  String content,
  String createdAt,
  String updatedAt
) {
}
