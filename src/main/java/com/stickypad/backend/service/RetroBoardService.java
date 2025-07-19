package com.stickypad.backend.service;

import com.stickypad.backend.model.RetroBoard;
import com.stickypad.backend.repository.HashMapBasedRetroBoardRepository;
import org.springframework.stereotype.Service;

@Service
public class RetroBoardService {

  private final HashMapBasedRetroBoardRepository hashMapBasedRetroBoardRepository;


  public RetroBoardService(HashMapBasedRetroBoardRepository hashMapBasedRetroBoardRepository) {
    this.hashMapBasedRetroBoardRepository = hashMapBasedRetroBoardRepository;
  }


  public RetroBoard createRetroBoard(String hostUserId) {
    return hashMapBasedRetroBoardRepository.createRetroBoard(hostUserId);
  }

}
