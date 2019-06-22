package com.franklin.sample.commit;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * The command line runner for the application
 */

@Component
public class LogServiceRunner implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceRunner.class);

  private final LogServiceFactory logServiceFactory;

  @Autowired
  public LogServiceRunner(LogServiceFactory logServiceFactory) {
    this.logServiceFactory = logServiceFactory;
  }

  @Override
  public void run(String... arguments) {
    Args command = new Args();
    JCommander.newBuilder()
            .addObject(command)
            .build()
            .parse(arguments);
    LogService logService = logServiceFactory.createLogService(command.getMode());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        logService.shutdown();
        logService.join();
      } catch (InterruptedException e) {
        LOGGER.warn(e.getMessage(), e);
      }
    }));
    logService.start();
  }
}
