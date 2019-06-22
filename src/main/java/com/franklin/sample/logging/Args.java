package com.franklin.sample.logging;

import com.beust.jcommander.Parameter;

/**
 * Command Line argument class
 */
public class Args {

  @Parameter(names = "-mode", description = "mode to run", required = true)
  private Mode mode;

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }
}
