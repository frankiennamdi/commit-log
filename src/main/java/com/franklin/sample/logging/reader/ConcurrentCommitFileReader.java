package com.franklin.sample.logging.reader;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Allows multiple threads with the same {@link #commitId} to read a file, with
 * each thread reading the next unread line
 */
public final class ConcurrentCommitFileReader implements Closeable {

  private final Scanner scanner;

  private final ReentrantLock reentrantLock = new ReentrantLock();

  private final String commitId;

  /**
   * Constructor
   *
   * @param file file to be read
   */
  public ConcurrentCommitFileReader(File file, String commitId) {
    try {
      this.scanner = new Scanner(file);
      this.commitId = commitId;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Read the next line
   *
   * @returns
   */
  public Optional<String> nextLine() throws InterruptedException {
    ReentrantLock lock = this.reentrantLock;
    lock.lockInterruptibly();
    try {
      if (scanner.hasNext()) {
        String line = scanner.nextLine();
        String fileCommitId = StringUtils.substringBefore(line, ":").trim();
        return fileCommitId.equals(commitId) ? Optional.of(line) : Optional.empty();
      } else {
        return Optional.empty();
      }
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void close() {
    scanner.close();
  }
}