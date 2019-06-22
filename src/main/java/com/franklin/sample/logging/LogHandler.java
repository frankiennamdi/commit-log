package com.franklin.sample.logging;

/**
 * Base interface for LogHandler, worker for the {@link LogServer}
 */

public interface LogHandler extends Runnable {

  void stop();

}
