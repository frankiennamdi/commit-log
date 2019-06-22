package com.franklin.sample.logging.reader;

import com.franklin.sample.logging.Config;
import com.franklin.sample.logging.LogHandler;
import com.franklin.sample.logging.LogServer;
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
import java.util.concurrent.TimeUnit;

public class ReadServer extends LogServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReadServer.class);

  private volatile boolean running;

  private final ExecutorService executorService;

  private final List<ReadHandler> readHandlers;

  public ReadServer(Config config, Path filePath) {
    int total = config.getReaders().values().stream().mapToInt(Integer::intValue).sum();
    this.executorService = Executors.newFixedThreadPool(total,
            new CustomizableThreadFactory(Mode.READER.name() + "-"));
    this.readHandlers = readHandlers(config, filePath);
  }

  @Override
  public void run() {
    LOGGER.info("Running in {} mode", Mode.READER.name());
    running = true;
    readHandlers.forEach(executorService::submit);
    int sleepTimeInSeconds = 5;
    while (running) {

      try {

        TimeUnit.SECONDS.sleep(sleepTimeInSeconds);

      } catch (InterruptedException e) {

        LOGGER.warn("Thread Interrupted");
        Thread.interrupted();

      }
    }
    LOGGER.info("ReadServer shutdown");
  }

  private List<ReadHandler> readHandlers(Config config, Path filePath) {
    List<ReadHandler> readHandlers = Lists.newArrayList();
    for (Map.Entry<String, Integer> reader : config.getReaders().entrySet()) {

      ConcurrentCommitFileReader concurrentCommitFileReader = new ConcurrentCommitFileReader(filePath.toFile(), reader.getKey());

      for (int i = 0; i < reader.getValue(); i++) {

        readHandlers.add(new ReadHandler(concurrentCommitFileReader));

      }
    }
    return readHandlers;
  }

  public void shutdown() {
    readHandlers.forEach(LogHandler::stop);
    running = false;
    executorService.shutdown();
  }
}
