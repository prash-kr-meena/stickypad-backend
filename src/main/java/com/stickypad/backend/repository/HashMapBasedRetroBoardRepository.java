package com.stickypad.backend.repository;

import com.stickypad.backend.model.RetroBoard;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class HashMapBasedRetroBoardRepository implements RetroBoardRepository {

  private final Map<Integer, RetroBoard> retroBoards = new ConcurrentHashMap<>();
  private final String applicationBaseUrl;


  public HashMapBasedRetroBoardRepository(
    @Value("${application.base.url}") String applicationBaseUrl
  ) {
    this.applicationBaseUrl = applicationBaseUrl;
  }


  @Override
  public RetroBoard createRetroBoard(String hostUserId) {
    int boardNumber = generateUniqueBoardNumber();
    String boardLink = URI.create(applicationBaseUrl + "retro-board" + boardNumber).toString();
    RetroBoard retroBoard = RetroBoard.builder()
      .boardNumber(boardNumber)
      .hostUserId(hostUserId)
      .boardLink(boardLink)
      .userStatusMap(Map.of())
      .notes(List.of())
      .build();

    retroBoards.put(boardNumber, retroBoard);
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
  public int generateUniqueBoardNumber() {
    int number = (int) (Math.random() * 9000) + 1000;
    if (retroBoards.containsKey(number)) {
      // If the number already exists, generate a new one
      return generateUniqueBoardNumber();
    }
    return number;
  }

}
