package com.franklin.sample.commit.reader;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Allows multiple threads with the same {@link #commitId} to read the commit log file, with
 * each thread reading the next unread line, sleeping if no lines are available yet.
 */
public final class ConcurrentCommitFileReader implements Closeable {

  private final Scanner scanner;

  private final Object fileAccess = new Object();

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
  public Optional<String> nextLine() {
    synchronized (fileAccess) {
      if (scanner.hasNext()) {
        String line = scanner.nextLine();
        String fileCommitId = StringUtils.substringBefore(line, ":").trim();
        return fileCommitId.equals(commitId) ? Optional.of(line) : Optional.empty();
      } else {
        return Optional.empty();
      }
    }
  }

  @Override
  public void close() {
    synchronized (fileAccess) {
      scanner.close();
    }
  }
}