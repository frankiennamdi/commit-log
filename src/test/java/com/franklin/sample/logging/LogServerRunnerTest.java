package com.franklin.sample.logging;

import com.franklin.sample.logging.reader.ReadServer;
import com.franklin.sample.logging.writer.WriteServer;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LogServerRunnerTest {

  @Test
  public void testRun() {
    Config config = new Config();
    config.setWriters(ImmutableMap.of());
    config.setReaders(ImmutableMap.of());
    config.setLogPath("path");

    LogServerFactory logServerFactory = mock(LogServerFactory.class);
    WriteServer writeServer = mock(WriteServer.class);
    ReadServer readServer = mock(ReadServer.class);

    when(logServerFactory.createLogServer(Mode.WRITER)).thenReturn(writeServer);
    when(logServerFactory.createLogServer(Mode.READER)).thenReturn(readServer);

    LogServerRunner logServerRunner = new LogServerRunner(logServerFactory);
    logServerRunner.run("-mode", "READER");
    logServerRunner.run("-mode", "WRITER");

    verify(logServerFactory).createLogServer(Mode.WRITER);
    verify(logServerFactory).createLogServer(Mode.READER);
    verify(writeServer).start();
    verify(readServer).start();
  }
}
