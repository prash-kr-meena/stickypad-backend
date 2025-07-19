package com.stickypad.backend.repository;

import com.stickypad.backend.model.RetroBoard;

public interface RetroBoardRepository {

  RetroBoard createRetroBoard(String hostUserId);

}
