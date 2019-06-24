package com.franklin.sample.commit;

/**
 * Base class for LogService extends {@link Thread}
 */

public abstract class LogService extends Thread {

  public abstract void shutdown() throws InterruptedException;

}
