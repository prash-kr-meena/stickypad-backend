package com.stickypad.backend.model;

import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record RetroBoard(
  Integer boardNumber,
  String boardLink,
  String hostUserId,
  Map<String, String> userStatusMap,
  List<Notes> notes
) {
}
