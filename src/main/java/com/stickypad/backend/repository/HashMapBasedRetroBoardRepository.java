package com.stickypad.backend.repository;

import com.stickypad.backend.exceptions.RetroBoardNotFound;
import com.stickypad.backend.model.Note;
import com.stickypad.backend.model.RetroBoard;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class HashMapBasedRetroBoardRepository implements RetroBoardRepository {

  private final Map<Integer, RetroBoard> retroBoards = new ConcurrentHashMap<>();
  private final String appBaseUrl;


  public HashMapBasedRetroBoardRepository(
    @Value("${application.base.url}") String appBaseUrl
  ) {
    this.appBaseUrl = appBaseUrl;
  }


  @Override
  public RetroBoard createRetroBoard(String hostUserId) {
    Integer boardId = generateUniqueBoardId();
    RetroBoard retroBoard = RetroBoard.create(boardId, hostUserId, appBaseUrl);;
//    RetroBoard retroBoard = new RetroBoard(boardId, hostUserId, appBaseUrl);
    retroBoards.put(boardId, retroBoard);
    return retroBoard;
  }


  @Override
  public RetroBoard addNote(Integer boardId, Note note) {
    retroBoardExists(boardId);
    return retroBoards.get(boardId).addNote(note);
  }


  @Override
  public RetroBoard getRetroBoard(Integer boardId) {
    retroBoardExists(boardId);
    return retroBoards.get(boardId);
  }


  /**
   * Generates a unique board number for a retro board. The number is a random integer between 1000 and 9999.
   * <p>
   * Note: It would make sure the number it generated is unique by checking against existing board numbers in a real
   * application.
   *
   * @return A unique board number.
   */
  public Integer generateUniqueBoardId() {
    Integer number = (int) (Math.random() * 9000) + 1000;
    if (retroBoards.containsKey(number)) {
      // If the number already exists, generate a new one
      return generateUniqueBoardId();
    }
    return number;
  }


  @Override
  public void retroBoardExists(Integer boardId) {
    if (!retroBoards.containsKey(boardId)) {
      throw new RetroBoardNotFound("Board with ID " + boardId + " does not exist.");
    }
  }

}
