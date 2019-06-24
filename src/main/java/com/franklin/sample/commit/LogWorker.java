package com.franklin.sample.commit;

/**
 * Base interface for LogWorker, worker for the {@link LogService}
 */

public interface LogWorker extends Runnable {

  void stop();

}
