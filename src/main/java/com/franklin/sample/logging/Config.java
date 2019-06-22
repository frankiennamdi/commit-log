package com.franklin.sample.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * LogService configuration
 */

@Component
@ConfigurationProperties(prefix = "app.log-service")
public class Config {

  private Map<String, Integer> writers;

  private Map<String, Integer> readers;

  private String logPath;

  public Map<String, Integer> getWriters() {
    return writers;
  }

  public void setWriters(Map<String, Integer> writers) {
    this.writers = writers;
  }

  public Map<String, Integer> getReaders() {
    return readers;
  }

  public void setReaders(Map<String, Integer> readers) {
    this.readers = readers;
  }

  public String getLogPath() {
    return logPath;
  }

  public void setLogPath(String logPath) {
    this.logPath = logPath;
  }
}
