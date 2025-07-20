package com.stickypad.backend.service;

import com.stickypad.backend.model.Note;
import com.stickypad.backend.model.RetroBoard;
import com.stickypad.backend.repository.RetroBoardRepository;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

@Service
public class RetroBoardService {

  private final Long pollingTimeout;
  private final RetroBoardRepository retroBoardRepository;
  private final RetroBoardDeferredClientsManager deferredResultManager;


  public RetroBoardService(
    RetroBoardRepository retroBoardRepository,
    RetroBoardDeferredClientsManager deferredResultManager,
    @Value("${retro-board.polling.timeout}") Long pollingTimeout
  ) {
    this.pollingTimeout = pollingTimeout;
    this.retroBoardRepository = retroBoardRepository;
    this.deferredResultManager = deferredResultManager;
  }


  public RetroBoard createRetroBoard(String hostUserId) {
    return retroBoardRepository.createRetroBoard(hostUserId);
  }

  public RetroBoard getRetroBoard(Integer boardId) {
    return retroBoardRepository.getRetroBoard(boardId);
  }


  public RetroBoard addNote(Integer boardId, String content, String userId) {
    Note note = Note.create(content, userId);
    RetroBoard retroBoard = retroBoardRepository.addNote(boardId, note);

    // Notify all clients that, there is a new note added to the retro board
    deferredResultManager.notifyAll(boardId, retroBoard);
    return retroBoard;
  }


  public RetroBoard deleteNote(Integer boardId, String noteId) {
    RetroBoard retroBoard = getRetroBoard(boardId); // Ensure the board exists
    return retroBoard.deleteNote(noteId);
  }


  public RetroBoard updateNote(Integer boardId, String noteId, String content, String userId) {
    RetroBoard retroBoard = getRetroBoard(boardId); // Ensure the board exists
    return retroBoard.updateNote(noteId, content, userId);
  }


  public DeferredResult<RetroBoard> pollRetroBoard(Integer boardId, Optional<Integer> clientVersion) {
    RetroBoard retroBoard = retroBoardRepository.getRetroBoard(boardId);

    if (clientVersion.isPresent() && retroBoard.newVersionAvailable(clientVersion.get())) {
      DeferredResult<RetroBoard> result = new DeferredResult<>(pollingTimeout);
      result.setResult(retroBoard); // Return the current state of the board immediately if a new version is available
      return result;
    }

    DeferredResult<RetroBoard> result = new DeferredResult<>(pollingTimeout);
    deferredResultManager.add(boardId, result);

    result.onTimeout(() -> {
      deferredResultManager.remove(boardId, result);
      result.setErrorResult(
        ResponseEntity
          .status(HttpStatus.REQUEST_TIMEOUT)
          .body("Polling timed out. Please try again later.")
      );
    });

    return result;
  }

}
