package com.stickypad.backend.repository;

import com.stickypad.backend.model.Note;
import com.stickypad.backend.model.RetroBoard;

public interface RetroBoardRepository {

  RetroBoard createRetroBoard(String hostUserId);

  void boardExists(Integer boardId);

  RetroBoard addNote(Integer boardId, Note note);

}
