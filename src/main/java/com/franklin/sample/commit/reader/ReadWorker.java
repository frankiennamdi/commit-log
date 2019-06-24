package com.franklin.sample.commit.reader;

import com.franklin.sample.commit.LogWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Worker for reading file.
 */
class ReadWorker implements LogWorker {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReadWorker.class);

  private volatile boolean running = true;

  private final ConcurrentCommitFileReader concurrentCommitFileReader;

  ReadWorker(ConcurrentCommitFileReader concurrentCommitFileReader) {
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
          TimeUnit.MILLISECONDS.sleep(sleepTimeInMillis);
        } else {
          TimeUnit.MILLISECONDS.sleep(sleepTimeInMillis);
        }
      } catch (InterruptedException e) {
        LOGGER.warn("Thread Interrupted {}");
      }
    }
    concurrentCommitFileReader.close();
    LOGGER.info("Handler Shutdown");
  }
}
