package com.franklin.sample.logging;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.test.rule.OutputCapture;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;

public class WriteAndReadServiceTest {

  @Rule
  public OutputCapture outputCapture = new OutputCapture();

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void testReadAndWriteCommitLog() throws InterruptedException, IOException {
    String path = temporaryFolder.newFolder().getAbsolutePath();
    String filePath = Joiner.on("/").join(path, "commit.log");
    Map<String, Integer> threadConfig = ImmutableMap.<String, Integer>builder()
            .put("A", 2)
            .put("B", 3)
            .build();
    Config config = new Config();
    config.setLogPath(filePath);
    config.setWriters(threadConfig);
    config.setReaders(threadConfig);

    LogServiceFactory logServiceFactory = new LogServiceFactory(config);

    LogService writeServer = logServiceFactory.createLogService(Mode.WRITER);
    writeServer.start();

    LogService readServer = logServiceFactory.createLogService(Mode.READER);
    readServer.start();

    Thread.sleep(3000);


    writeServer.shutdown();
    readServer.shutdown();
    writeServer.join();
    readServer.join();

    String output = outputCapture.toString();
    String[] lines = output.split("\n");
    List<String> readMessages = Arrays.stream(lines)
            .filter(line -> line.matches("\\[READER-\\d+]\\s-\\s\\w+:\\s\\d+:\\s\\w+"))
            .map(line -> StringUtils.substringAfterLast(line, "-").trim())
            .collect(Collectors.toList());

    List<String> fileLines = FileUtils.readLines(new File(filePath), StandardCharsets.UTF_8);
    assertThat(readMessages, everyItem(isIn(fileLines)));
  }
}
