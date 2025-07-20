package com.stickypad.backend.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record Note(
  String id,
  String userId,
  String content,
  String createdAt,
  String updatedAt
) {

  public static Note create(String content, String userId) {
    return Note.builder()
      .id(UUID.randomUUID().toString())
      .userId(userId)
      .content(content)
      .createdAt(Instant.now().toString())  // Time in UTC, so that it is consistent across different time zones
      .updatedAt(Instant.now().toString())
      .build();
  }


  public Note getNewNote(String newContent) {
    return Note.builder()
      .id(id) // Keep the same ID for updates
      .userId(userId)
      .content(newContent)
      .createdAt(createdAt) // Keep the original creation time
      .updatedAt(Instant.now().toString())  // Update the time to now for the updated note
      .build();
  }

}
