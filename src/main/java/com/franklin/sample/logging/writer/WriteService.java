package com.franklin.sample.logging.writer;

import com.franklin.sample.logging.Config;
import com.franklin.sample.logging.LogHandler;
import com.franklin.sample.logging.LogService;
import com.franklin.sample.logging.Mode;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class WriteService extends LogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WriteService.class);

  private final ExecutorService executorService;

  private final List<LogHandler> writeHandlers;

  private volatile boolean running;

  public WriteService(Config config, Path filePath) {
    ThreadFactory threadFactory = new CustomizableThreadFactory(Mode.WRITER.name() + "-");
    int totalNumOfWriterThreads = config.getWriters().values().stream().mapToInt(Integer::intValue).sum();
    this.executorService = Executors.newFixedThreadPool(totalNumOfWriterThreads, threadFactory);
    this.writeHandlers = writeHandlers(config, filePath);
  }

  private List<LogHandler> writeHandlers(Config config, Path filePath) {

    List<LogHandler> writeHandlers = Lists.newArrayList();

    for (Map.Entry<String, Integer> writer : config.getWriters().entrySet()) {

      WriterCommitIdentification writerCommitIdentification = new WriterCommitIdentification(writer.getKey());
      for (int i = 0; i < writer.getValue(); i++) {
        writeHandlers.add(new WriteHandler(filePath, writerCommitIdentification));
      }

    }
    return writeHandlers;
  }

  @Override
  public void run() {
    LOGGER.info("Running in {} mode", Mode.WRITER.name());
    running = true;
    writeHandlers.forEach(executorService::submit);
    int sleepTimeInSeconds = 5;
    while (running) {

      try {

        TimeUnit.SECONDS.sleep(sleepTimeInSeconds);

      } catch (InterruptedException e) {
        LOGGER.warn("Thread Interrupted");
        Thread.interrupted();
      }
    }
    LOGGER.info("WriteService shutdown");
  }

  public void shutdown() {
    writeHandlers.forEach(LogHandler::stop);
    running = false;
    executorService.shutdown();
  }
}
