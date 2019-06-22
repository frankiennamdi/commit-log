package com.franklin.sample.commit;

import com.franklin.sample.commit.reader.ReadService;
import com.franklin.sample.commit.writer.WriteService;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LogServiceRunnerTest {

  @Test
  public void testRun() {
    Config config = new Config();
    config.setWriters(ImmutableMap.of());
    config.setReaders(ImmutableMap.of());
    config.setLogPath("path");

    LogServiceFactory logServiceFactory = mock(LogServiceFactory.class);
    WriteService writeService = mock(WriteService.class);
    ReadService readService = mock(ReadService.class);

    when(logServiceFactory.createLogService(Mode.WRITER)).thenReturn(writeService);
    when(logServiceFactory.createLogService(Mode.READER)).thenReturn(readService);

    LogServiceRunner logServiceRunner = new LogServiceRunner(logServiceFactory);
    logServiceRunner.run("-mode", "READER");
    logServiceRunner.run("-mode", "WRITER");

    verify(logServiceFactory).createLogService(Mode.WRITER);
    verify(logServiceFactory).createLogService(Mode.READER);
    verify(writeService).start();
    verify(readService).start();
  }
}
