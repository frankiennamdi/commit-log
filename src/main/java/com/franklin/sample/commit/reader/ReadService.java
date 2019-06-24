package com.franklin.sample.commit.reader;

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
 * Provide reading services for the application
 */
public class ReadService extends LogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReadService.class);

  private volatile boolean running;

  private final List<Thread> workerThreads = new ArrayList<>();

  private final List<ReadWorker> readWorkers = new ArrayList<>();

  public ReadService(Config config, Path filePath) {
    ThreadFactory threadFactory = new CustomizableThreadFactory(Mode.READER.name() + "-");
    List<ReadWorker> workers = createReadWorkers(config, filePath);
    for (ReadWorker readWorker : workers) {
      workerThreads.add(threadFactory.newThread(readWorker));
      readWorkers.add(readWorker);
    }
  }

  @Override
  public void run() {
    LOGGER.info("Running in {} mode", Mode.READER.name());
    if (workerThreads.isEmpty() || readWorkers.isEmpty()) {
      throw new RuntimeException("No Reader Threads or Workers");
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
    LOGGER.info("ReadService shutdown");
  }

  private List<ReadWorker> createReadWorkers(Config config, Path filePath) {
    List<ReadWorker> readHandlers = Lists.newArrayList();
    for (Map.Entry<String, Integer> reader : config.getReaders().entrySet()) {

      ConcurrentCommitFileReader concurrentCommitFileReader = new ConcurrentCommitFileReader(filePath.toFile(), reader.getKey());

      for (int i = 0; i < reader.getValue(); i++) {

        readHandlers.add(new ReadWorker(concurrentCommitFileReader));

      }
    }
    return readHandlers;
  }

  public void shutdown() throws InterruptedException {
    readWorkers.forEach(LogWorker::stop);
    for (Thread thread : workerThreads) {
      thread.join();
    }
    running = false;
  }
}
