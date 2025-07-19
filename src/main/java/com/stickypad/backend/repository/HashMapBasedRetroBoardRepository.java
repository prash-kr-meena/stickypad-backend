package com.stickypad.backend.repository;

import static com.stickypad.backend.model.RetroBoard.UserStatus.in_progress;

import com.stickypad.backend.model.Note;
import com.stickypad.backend.model.RetroBoard;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
    int boardId = generateUniqueBoardId();
    RetroBoard retroBoard = RetroBoard.builder()
      .id(boardId)
      .hostUserId(hostUserId)
      .boardLink(URI.create(appBaseUrl + "retro-board/" + boardId).toString())
      .userStatus(new ConcurrentHashMap<>()) // Using ConcurrentHashMap for thread-safe operations on user status
      .notes(new CopyOnWriteArrayList<>()) // Using CopyOnWriteArrayList for thread-safe operations on notes
      .build();

    // Add the host user to status
    retroBoard.userStatus().put(hostUserId, in_progress);

    retroBoards.put(boardId, retroBoard);
    return retroBoard;
  }


  @Override
  public void boardExists(Integer boardId) {
    if (!retroBoards.containsKey(boardId)) {
      throw new IllegalArgumentException("Board with ID " + boardId + " does not exist.");
    }
  }


  @Override
  public RetroBoard addNote(Integer boardId, Note note) {
    boardExists(boardId);
    RetroBoard retroBoard = retroBoards.get(boardId);
    retroBoard.notes().add(note);

    // Update user status to in_progress
    retroBoard.userStatus().put(note.userId(), in_progress);
    return retroBoard;
  }


  /**
   * Generates a unique board number for a retro board. The number is a random integer between 1000 and 9999.
   * <p>
   * Note: It would make sure the number it generated is unique by checking against existing board numbers in a real
   * application.
   *
   * @return A unique board number.
   */
  public int generateUniqueBoardId() {
    int number = (int) (Math.random() * 9000) + 1000;
    if (retroBoards.containsKey(number)) {
      // If the number already exists, generate a new one
      return generateUniqueBoardId();
    }
    return number;
  }

}
