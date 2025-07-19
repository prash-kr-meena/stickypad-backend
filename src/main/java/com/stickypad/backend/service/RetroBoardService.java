package com.stickypad.backend.service;

import com.stickypad.backend.model.Note;
import com.stickypad.backend.model.RetroBoard;
import com.stickypad.backend.repository.RetroBoardRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RetroBoardService {

  private final RetroBoardRepository retroBoardRepository;


  public RetroBoardService(RetroBoardRepository retroBoardRepository) {
    this.retroBoardRepository = retroBoardRepository;
  }


  public RetroBoard createRetroBoard(String hostUserId) {
    return retroBoardRepository.createRetroBoard(hostUserId);
  }


  public RetroBoard addNote(Integer boardId, String content, String userId) {
    Note note = Note.builder()
      .noteId(UUID.randomUUID().toString())
      .userId(userId)
      .content(content)
      .createdAt(Instant.now().toString())  // Time in UTC, so that it is consistent across different time zones
      .updatedAt(Instant.now().toString())
      .build();

    return retroBoardRepository.addNote(boardId, note);
  }

}
