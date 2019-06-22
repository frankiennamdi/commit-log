package com.franklin.sample.logging.writer;

import com.franklin.sample.logging.*;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class WriteServiceTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void testWriteAndRead() throws IOException, InterruptedException {
    String path = temporaryFolder.newFolder().getAbsolutePath();
    Config config = new Config();
    config.setWriters(ImmutableMap.<String, Integer>builder()
            .put("A", 2)
            .put("B", 3)
            .build());

    String filePath = Joiner.on("/").join(path, "commit.log");
    config.setLogPath(filePath);
    LogService logService = new LogServiceFactory(config).createLogService(Mode.WRITER);
    logService.start();
    Thread.sleep(2000);
    logService.shutdown();
    logService.join();

    List<String> fileLines = FileUtils.readLines(new File(filePath), StandardCharsets.UTF_8);
    String regex = "(?<cid>\\w+):\\s(?<uuid>\\d+)";
    Pattern pattern = Pattern.compile(regex);
    Optional<Map.Entry<String, Long>> duplicateCommits = fileLines.stream()
            .map(line -> {
              Matcher matcher = pattern.matcher(line);
              if (matcher.find()) {
                return matcher.group("cid") + "-" + matcher.group("uuid");
              } else {
                return "";
              }
            })
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet().stream()
            .filter(entry -> entry.getValue() > 1L)
            .findAny();

    assertThat(duplicateCommits.isPresent(), is(false));
  }
}
