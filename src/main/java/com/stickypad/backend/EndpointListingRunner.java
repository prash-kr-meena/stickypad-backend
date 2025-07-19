package com.stickypad.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
@Slf4j
public class EndpointListingRunner implements CommandLineRunner {

  private final RequestMappingHandlerMapping handlerMapping;


  public EndpointListingRunner(
    @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping
  ) {
    this.handlerMapping = handlerMapping;
  }


  @Override
  public void run(String... args) {
    handlerMapping
      .getHandlerMethods()
      .forEach((key, value) -> {
        String patterns = key.getPatternValues().toString();
        patterns= String.format("%-25s", patterns);

        String methods = key.getMethodsCondition().getMethods().toString();
        methods= String.format("%-6s", methods);

        log.info("{} {} : {}", methods, patterns, value);
      });
  }

}