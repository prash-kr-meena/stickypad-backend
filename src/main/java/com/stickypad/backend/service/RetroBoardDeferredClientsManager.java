package com.stickypad.backend.service;

import com.stickypad.backend.model.RetroBoard;
import com.stickypad.backend.repository.RetroBoardRepository;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

@Service
public class RetroBoardDeferredClientsManager {

  private final RetroBoardRepository retroBoardRepository;
  private final Map<Integer, Queue<DeferredResult<RetroBoard>>> deferredClientsByBoard = new ConcurrentHashMap<>();


  public RetroBoardDeferredClientsManager(RetroBoardRepository retroBoardRepository) {
    this.retroBoardRepository = retroBoardRepository;
  }


  /**
   * Adds a DeferredResult for a specific board ID.
   *
   * @param boardId
   *   the ID of the retro board
   * @param deferredResult
   *   the DeferredResult to be added
   */
  public void add(Integer boardId, DeferredResult<RetroBoard> deferredResult) {
    retroBoardRepository.retroBoardExists(boardId);
    deferredClientsByBoard
      .computeIfAbsent(boardId, _ -> new ConcurrentLinkedQueue<>())// for thread-safe operations
      .add(deferredResult);
  }


  /**
   * Removes a DeferredResult for a specific board ID.
   *
   * @param boardId
   *   the ID of the retro board
   * @param result
   *   the DeferredResult to be removed
   */
  public void remove(int boardId, DeferredResult<RetroBoard> result) {
    retroBoardRepository.retroBoardExists(boardId);
    deferredClientsByBoard.get(boardId).remove(result);
  }


  /**
   * Notifies all waiting clients that something changed there is a new note added to the retro board.
   *
   * @param boardId
   *   the ID of the retro board
   * @param retroBoard
   */
  public void notifyAll(Integer boardId, RetroBoard retroBoard) {
    // No client might have connected for any updates of the board
    if (!deferredClientsByBoard.containsKey(boardId)) {
      return;
    }

    Queue<DeferredResult<RetroBoard>> deferredResultsOfBoard = deferredClientsByBoard.get(boardId);
    for (DeferredResult<RetroBoard> deferredResult : deferredResultsOfBoard) {
      deferredResult.setResult(retroBoard);
      deferredResultsOfBoard.remove(deferredResult);
    }
  }

}
