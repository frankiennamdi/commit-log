package com.franklin.sample.commit.writer;

import com.franklin.sample.commit.LogWorker;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Worker for writing
 */
class WriteWorker implements LogWorker {

  private static final Logger LOGGER = LoggerFactory.getLogger(WriteWorker.class);

  private final FileWriter fileWriter;

  private volatile boolean running = true;

  WriteWorker(Path filePath, WriterCommitIdentification writerCommitIdentification) {
    this.fileWriter = new FileWriter(filePath, writerCommitIdentification);
  }

  @Override
  public void run() {
    int min = 10;
    int max = 30;
    int thinkTimeInMills = 500;
    while (running) {
      try {

        int length = ThreadLocalRandom.current().nextInt(min, max + 1);

        String data = RandomStringUtils.randomAlphabetic(length);
        String msg = fileWriter.write(data);

        TimeUnit.MILLISECONDS.sleep(thinkTimeInMills);
        LOGGER.info(msg);

      } catch (IOException | InterruptedException ex) {
        LOGGER.warn(ex.getMessage(), ex);
        throw new RuntimeException(ex);
      }
    }

    LOGGER.info("Handler Shutdown");
  }

  public void stop() {
    running = false;
  }
}
