package com.franklin.sample.commit;

/**
 * Base interface for LogHandler, worker for the {@link LogService}
 */

public interface LogHandler extends Runnable {

  void stop();

}
