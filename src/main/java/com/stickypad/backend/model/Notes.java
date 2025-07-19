package com.stickypad.backend.model;

public record Notes(
  String noteId,
  String userId,
  String content,
  String createdAt,
  String updatedAt
) {
}
