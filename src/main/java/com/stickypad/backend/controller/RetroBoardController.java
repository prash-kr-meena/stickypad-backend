package com.stickypad.backend.controller;

import com.stickypad.backend.model.CreateRetroBoardRequest;
import com.stickypad.backend.model.RetroBoard;
import com.stickypad.backend.service.RetroBoardService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/v1/api/")
public class RetroBoardController {

  private final RetroBoardService retroBoardService;


  public RetroBoardController(RetroBoardService retroBoardService) {
    this.retroBoardService = retroBoardService;
  }


  @PostMapping("retro-board")
  public RetroBoard createRetroBoard(
    @RequestBody CreateRetroBoardRequest createRetroBoardRequest
  ) {
    return retroBoardService.createRetroBoard(createRetroBoardRequest.hostUserId());
  }

}
