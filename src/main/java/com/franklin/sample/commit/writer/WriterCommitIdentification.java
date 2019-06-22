package com.franklin.sample.commit.writer;

import java.util.concurrent.atomic.AtomicLong;

class WriterCommitIdentification {

  private final String id;

  private final AtomicLong uuid = new AtomicLong(1);

  WriterCommitIdentification(String id) {
    this.id = id;
  }

  String getId() {
    return id;
  }

  long getUuid() {
    return uuid.getAndAdd(1);
  }
}
