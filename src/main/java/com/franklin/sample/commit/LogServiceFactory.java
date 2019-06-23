package com.franklin.sample.commit;

import com.franklin.sample.commit.reader.ReadService;
import com.franklin.sample.commit.writer.WriteService;
import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Factory for constructing {@link LogService}
 */

@Component
public class LogServiceFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceFactory.class);

  private final Config config;

  @Autowired
  public LogServiceFactory(Config config) {
    this.config = config;
  }

  public LogService createLogService(Mode mode) {
    File file = new File(config.getLogPath());
    if (!file.isAbsolute()) {
      String path = Paths.get("").toAbsolutePath().toString();
      file = new File(Joiner.on("/").join(path, config.getLogPath()));
    }

    LOGGER.info("Starting Log Service in {} mode", mode.name());
    LOGGER.info("Log Location {}", file.getAbsolutePath());

    try {
      if (mode.equals(Mode.WRITER)) {
        LOGGER.info("Writers {}", Arrays.toString(config.getWriters().entrySet().toArray()));
        FileUtils.deleteQuietly(file);
        FileUtils.touch(file);
        return new WriteService(config, file.toPath());

      } else if (mode.equals(Mode.READER)) {
        LOGGER.info("Readers {}", Arrays.toString(config.getReaders().entrySet().toArray()));
        if (!Files.exists(file.toPath())) {
          throw new RuntimeException("File does not exist " + file.getAbsolutePath());
        }
        return new ReadService(config, file.toPath());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return null;
  }
}
