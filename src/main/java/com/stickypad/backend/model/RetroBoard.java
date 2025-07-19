package com.stickypad.backend.model;

import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record RetroBoard(
  Integer id,
  String boardLink,
  String hostUserId,
  Map<String, UserStatus> userStatus,
  List<Note> notes
) {

  public enum UserStatus {
    in_progress,
    finished,
  }

}
