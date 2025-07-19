package com.stickypad.backend.controller;

import com.stickypad.backend.model.AddNoteRequest;
import com.stickypad.backend.model.CreateRetroBoardRequest;
import com.stickypad.backend.model.RetroBoard;
import com.stickypad.backend.service.RetroBoardService;
import jakarta.validation.Valid;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController()
@RequestMapping("/v1/api/")
public class RetroBoardController {

  private final RetroBoardService retroBoardService;
  private final Queue<DeferredResult<RetroBoard>> deferredResultsQueue = new ConcurrentLinkedQueue<>();


  public RetroBoardController(RetroBoardService retroBoardService) {
    this.retroBoardService = retroBoardService;
  }


  @PostMapping("retro-board")
  public RetroBoard createRetroBoard(@Valid @RequestBody CreateRetroBoardRequest createRetroBoardRequest) {
    return retroBoardService.createRetroBoard(createRetroBoardRequest.hostUserId());
  }


  /**
   * Polls the retro board for updates. This endpoint is used to get the latest state of the retro board. THis is HTTP
   * Long Polling endpoint, which means it will hold the connection open until there is an update
   *
   * @return RetroBoard
   */
  @GetMapping("retro-board/{boardId}/poll")
  public DeferredResult<RetroBoard> pollRetroBoard(@PathVariable String boardId) {
    DeferredResult<RetroBoard> result = new DeferredResult<>(10_000L);
    result.onTimeout(() -> {
      deferredResultsQueue.remove(result);
      result.setErrorResult(ResponseEntity
        .status(HttpStatus.REQUEST_TIMEOUT)
        .body("Polling timed out. Please try again later."));
    });
    deferredResultsQueue.add(result);
    return result;
  }


  @PostMapping("retro-board/{boardId}/note")
  public ResponseEntity<RetroBoard> addNote(
    @PathVariable Integer boardId,
    @Valid @RequestBody AddNoteRequest addNoteRequest
  ) {
    RetroBoard retroBoard = retroBoardService.addNote(boardId, addNoteRequest.content(), addNoteRequest.userId());
    // Notify all waiting clients that there is a new note added
    for (DeferredResult<RetroBoard> deferredResult : deferredResultsQueue) {
      deferredResult.setResult(retroBoard);
      deferredResultsQueue.remove(deferredResult);
    }
    return ResponseEntity.ok().body(retroBoard);
  }

  //  @DeleteMapping("retro-board/{board}/note")

  //  @PutMapping("retro-board/{board}/note/{noteId}")


}
