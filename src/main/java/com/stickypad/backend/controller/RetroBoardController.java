package com.stickypad.backend.controller;

import com.stickypad.backend.model.NoteData;
import com.stickypad.backend.model.CreateRetroBoardRequest;
import com.stickypad.backend.model.RetroBoard;
import com.stickypad.backend.service.RetroBoardService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController()
@RequestMapping("/v1/api/")
public class RetroBoardController {

  private final RetroBoardService retroBoardService;


  public RetroBoardController(RetroBoardService retroBoardService) {
    this.retroBoardService = retroBoardService;
  }


  /**
   * Creates a new retro board. The host user is the user who creates the retro board
   *
   * @param createRetroBoardRequest
   *   the request object containing the host user ID
   *
   * @return the created RetroBoard object
   */
  @PostMapping("retro-board")
  public ResponseEntity<RetroBoard> createRetroBoard(
    @Valid @RequestBody CreateRetroBoardRequest createRetroBoardRequest
  ) {
    RetroBoard retroBoard = retroBoardService.createRetroBoard(createRetroBoardRequest.hostUserId());
    return ResponseEntity.status(HttpStatus.CREATED).body(retroBoard);
  }


  /**
   * Gets the retro board by its ID. Useful for scenario when the client connects to the retro board for the first time
   *
   * @param boardId
   *   the ID of the retro board
   *
   * @return the RetroBoard object
   */
  @GetMapping("retro-board/{boardId}")
  public ResponseEntity<RetroBoard> getRetroBoard(@PathVariable("boardId") Integer boardId) {
    RetroBoard retroBoard = retroBoardService.getRetroBoard(boardId);
    return ResponseEntity.ok(retroBoard);
  }


  @PostMapping("retro-board/{boardId}/note")
  public ResponseEntity<RetroBoard> addNote(
    @PathVariable Integer boardId,
    @Valid @RequestBody NoteData noteData
  ) {
    RetroBoard retroBoard = retroBoardService.addNote(boardId, noteData.content(), noteData.userId());
    return ResponseEntity.status(HttpStatus.CREATED).body(retroBoard);
  }


  @DeleteMapping("retro-board/{boardId}/note/{noteId}")
  public ResponseEntity<RetroBoard> deleteNote(@PathVariable Integer boardId, @PathVariable String noteId) {
    RetroBoard retroBoard = retroBoardService.deleteNote(boardId, noteId);
    return ResponseEntity.ok().body(retroBoard);
  }


  @PutMapping("retro-board/{boardId}/note/{noteId}")
  public ResponseEntity<RetroBoard> updateNote(
    @PathVariable Integer boardId,
    @PathVariable String noteId,
    @RequestBody @Valid NoteData noteData
  ) {
    RetroBoard retroBoard = retroBoardService.updateNote(boardId, noteId, noteData.content(), noteData.userId());
    return ResponseEntity.ok(retroBoard);
  }


  /**
   * Polls the retro board for updates. This endpoint is used to get the latest state of the retro board. This is HTTP
   * Long Polling endpoint, which means it will hold the connection open until there is an update
   * <p>
   * <p>
   * Note: this polling should only poll the for a particular retro board, not all boards.
   * </br>
   * <p>
   * Important:
   * <p>
   * This clientVersion will also handle the situations of race conditions or poor network reliability
   * </br>
   * e.g. if say after 10s the connection is timeout as no new data was found to be pushed to the client, but as soon as
   * the client connection breaks new notes were added to the board. but due to the poor network the client connection
   * took time to connect back and if the client does not have this clientVersion, there would be no way for the client
   * to know if it is missing some data and it would just be waiting for any new updates to be notified by the Server
   * but the older updates to the board would be lost.
   *
   * <p>
   * <p>
   * With this clientVersion, it would be able to tell the server what is the version of the board it has and if the
   * server has a newer version, it will return that.
   *
   * @param boardId
   *   ID of the board which client wants to poll for updates
   * @param clientVersion
   *   current version of the board that the client has. If the server has a newer version, it will return that. It can
   *   be null, in which case the server will return the latest version of the board.
   *
   * @return RetroBoard
   */
  @GetMapping("retro-board/{boardId}/poll")
  public DeferredResult<RetroBoard> pollRetroBoard(
    @PathVariable Integer boardId,
    @RequestParam("version") Optional<Integer> clientVersion
  ) {
    return retroBoardService.pollRetroBoard(boardId, clientVersion);
  }

}
