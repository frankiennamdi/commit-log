package com.franklin.sample.commit.reader;

import com.franklin.sample.commit.*;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;

public class ReadServiceTest {

  @Rule
  public OutputCapture outputCapture = new OutputCapture();

  @Test
  public void testReadFile() throws URISyntaxException, InterruptedException, IOException {
    ClassLoader classLoader = MoreObjects.firstNonNull(Thread.currentThread().getContextClassLoader(),
            ReadServiceTest.class.getClassLoader());
    URI uri = classLoader.getResource("sample_files/sample_commit.log").toURI();
    Path logPath = Paths.get(uri);

    Config config = new Config();
    config.setReaders(ImmutableMap.<String, Integer>builder()
            .put("A", 2)
            .put("B", 3)
            .build());
    config.setLogPath(logPath.toAbsolutePath().toString());
    LogService logService = new LogServiceFactory(config).createLogService(Mode.READER);
    logService.start();
    Thread.sleep(3000);
    logService.shutdown();
    logService.join();

    String output = outputCapture.toString();
    String[] lines = output.split("\n");
    List<String> readMessages = Arrays.stream(lines)
            .filter(line -> line.matches("\\[READER-\\d+]\\s-\\s\\w+:\\s\\d+:\\s\\w+"))
            .map(line -> StringUtils.substringAfterLast(line, "-").trim())
            .collect(Collectors.toList());

    List<String> fileLines = FileUtils.readLines(logPath.toFile(), StandardCharsets.UTF_8);
    assertThat(readMessages, everyItem(isIn(fileLines)));
  }
}
