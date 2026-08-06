package com.symphony.bdk.cli.command;

import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Base class for noun (container) commands such as {@code message} or {@code stream}. When invoked
 * without a verb subcommand, it prints its own usage and performs no Symphony call.
 */
public abstract class ContainerCommand implements Runnable {

  @Spec
  protected CommandSpec spec;

  @Override
  public void run() {
    spec.commandLine().usage(spec.commandLine().getOut());
  }
}
