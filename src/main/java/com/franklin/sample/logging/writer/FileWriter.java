package com.franklin.sample.logging.writer;

import org.slf4j.helpers.MessageFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class FileWriter {

  private final Path filePath;

  private final WriterCommitIdentification writerCommitIdentification;

  FileWriter(Path filePath, WriterCommitIdentification writerCommitIdentification) {
    this.filePath = filePath;
    this.writerCommitIdentification = writerCommitIdentification;
  }

  String write(String data) throws IOException {
    String msg = MessageFormatter.arrayFormat("{}: {}: {}",
            new String[]{writerCommitIdentification.getId(), Long.toString(writerCommitIdentification.getUuid()), data}).getMessage();
    Files.write(filePath, (msg + "\n").getBytes(), StandardOpenOption.APPEND);
    return msg;
  }

}
