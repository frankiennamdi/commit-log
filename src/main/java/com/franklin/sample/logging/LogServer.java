package com.franklin.sample.logging;

/**
 * Base class for LogServer extends {@link Thread}
 */

public abstract class LogServer extends Thread {

  public abstract void shutdown();

}
