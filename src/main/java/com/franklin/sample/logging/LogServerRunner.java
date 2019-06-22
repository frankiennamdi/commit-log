package com.franklin.sample.logging;

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
public class LogServerRunner implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogServerRunner.class);

  private final LogServerFactory logServerFactory;

  @Autowired
  public LogServerRunner(LogServerFactory logServerFactory) {
    this.logServerFactory = logServerFactory;
  }

  @Override
  public void run(String... arguments) {
    Args command = new Args();
    JCommander.newBuilder()
            .addObject(command)
            .build()
            .parse(arguments);
    LogServer logServer = logServerFactory.createLogServer(command.getMode());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        logServer.shutdown();
        logServer.join();
      } catch (InterruptedException e) {
        LOGGER.warn(e.getMessage(), e);
      }
    }));
    logServer.start();
  }
}
