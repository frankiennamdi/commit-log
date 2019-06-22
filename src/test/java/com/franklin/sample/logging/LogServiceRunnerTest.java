package com.franklin.sample.logging;

import com.franklin.sample.logging.reader.ReadService;
import com.franklin.sample.logging.writer.WriteService;
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
    WriteService writeServer = mock(WriteService.class);
    ReadService readServer = mock(ReadService.class);

    when(logServiceFactory.createLogService(Mode.WRITER)).thenReturn(writeServer);
    when(logServiceFactory.createLogService(Mode.READER)).thenReturn(readServer);

    LogServiceRunner logServiceRunner = new LogServiceRunner(logServiceFactory);
    logServiceRunner.run("-mode", "READER");
    logServiceRunner.run("-mode", "WRITER");

    verify(logServiceFactory).createLogService(Mode.WRITER);
    verify(logServiceFactory).createLogService(Mode.READER);
    verify(writeServer).start();
    verify(readServer).start();
  }
}
