package com.franklin.sample.commit.reader;

import com.franklin.sample.commit.LogHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

class ReadHandler implements LogHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReadHandler.class);

  private volatile boolean running = true;

  private final ConcurrentCommitFileReader concurrentCommitFileReader;

  ReadHandler(ConcurrentCommitFileReader concurrentCommitFileReader) {
    this.concurrentCommitFileReader = concurrentCommitFileReader;
  }

  @Override
  public void stop() {
    running = false;
  }

  @Override
  public void run() {
    int sleepTimeInMillis = 500;
    while (running) {
      try {
        Optional<String> nextLine = concurrentCommitFileReader.nextLine();
        if (nextLine.isPresent()) {
          LOGGER.info(nextLine.get());
        } else {
          TimeUnit.MILLISECONDS.sleep(sleepTimeInMillis);
        }
      } catch (InterruptedException e) {
        boolean interrupted = Thread.interrupted();
        LOGGER.warn("Thread Interrupted {}", interrupted);
      }
    }
    LOGGER.info("Handler Shutdown");
    concurrentCommitFileReader.close();
  }
}
