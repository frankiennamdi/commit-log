package com.franklin.sample.logging;

/**
 * Base class for LogService extends {@link Thread}
 */

public abstract class LogService extends Thread {

  public abstract void shutdown();

}
