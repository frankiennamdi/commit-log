package com.franklin.sample.commit.writer;

import com.franklin.sample.commit.Config;
import com.franklin.sample.commit.LogService;
import com.franklin.sample.commit.LogWorker;
import com.franklin.sample.commit.Mode;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Provide writing service for the application.
 */
public class WriteService extends LogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WriteService.class);

  private final List<Thread> workerThreads = new ArrayList<>();

  private final List<LogWorker> writeWorkers = new ArrayList<>();

  private volatile boolean running;

  public WriteService(Config config, Path filePath) {
    ThreadFactory threadFactory = new CustomizableThreadFactory(Mode.WRITER.name() + "-");
    List<WriteWorker> workers = createWriteWorkers(config, filePath);
    for (WriteWorker writeWorker : workers) {
      workerThreads.add(threadFactory.newThread(writeWorker));
      writeWorkers.add(writeWorker);
    }
  }

  private List<WriteWorker> createWriteWorkers(Config config, Path filePath) {

    List<WriteWorker> writeHandlers = Lists.newArrayList();

    for (Map.Entry<String, Integer> writer : config.getWriters().entrySet()) {

      WriterCommitIdentification writerCommitIdentification = new WriterCommitIdentification(writer.getKey());
      for (int i = 0; i < writer.getValue(); i++) {
        writeHandlers.add(new WriteWorker(filePath, writerCommitIdentification));
      }

    }
    return writeHandlers;
  }

  @Override
  public void run() {
    LOGGER.info("Running in {} mode", Mode.WRITER.name());
    if (workerThreads.isEmpty() || writeWorkers.isEmpty()) {
      throw new RuntimeException("No Writer Threads or Workers");
    }
    running = true;
    workerThreads.forEach(Thread::start);
    int sleepTimeInSeconds = 5;
    while (running) {

      try {

        TimeUnit.SECONDS.sleep(sleepTimeInSeconds);

      } catch (InterruptedException e) {
        LOGGER.warn("Thread Interrupted");
        throw new RuntimeException(e);
      }
    }
    LOGGER.info("WriteService shutdown");
  }

  public void shutdown() throws InterruptedException {
    writeWorkers.forEach(LogWorker::stop);
    for (Thread workerThread : workerThreads) {
      workerThread.join();
    }
    running = false;
  }
}
