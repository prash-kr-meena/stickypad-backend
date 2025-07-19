package com.stickypad.backend.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationStatusController {

  @Value("${app.version}")
  private String appVersion;


  @GetMapping("/")
  public ResponseEntity<Map<String, String>> healthCheck() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "StickyPad Application is up and running!");
    response.put("version", appVersion);
    return ResponseEntity.ok(response);
  }

}
