package com.franklin.sample.commit.writer;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Commit Identification for a writer. Also a provider of
 * unique id.
 */
class WriterCommitIdentification {

  private final String id;

  private final AtomicLong uuid = new AtomicLong(1);

  WriterCommitIdentification(String id) {
    this.id = id;
  }

  String getId() {
    return id;
  }

  long getNextUUID() {
    return uuid.getAndAdd(1);
  }
}
