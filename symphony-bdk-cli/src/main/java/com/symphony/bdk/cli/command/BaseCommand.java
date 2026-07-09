package com.symphony.bdk.cli.command;

import com.symphony.bdk.cli.BdkCli;
import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.exception.BdkConfigException;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.util.concurrent.Callable;

/**
 * Base class for leaf (verb) commands. Provides access to the shared {@link SymphonyBdk} facade and
 * a JSON emitter, both reached on the root {@link BdkCli} command via the parent-command chain.
 *
 * <p>Subclasses implement {@link Callable#call()}, returning an exit code (typically {@link
 * CommandLine.ExitCode#OK}). Any thrown exception is routed to the CLI's execution-exception
 * handler, which maps it to a JSON error envelope and a documented exit code.
 */
public abstract class BaseCommand implements Callable<Integer> {

  @Spec
  protected CommandSpec spec;

  /** The root command, reached from any depth in the subcommand tree. */
  protected BdkCli root() {
    return (BdkCli) spec.root().userObject();
  }

  /** The shared, lazily-authenticated BDK facade. */
  protected SymphonyBdk bdk()
      throws AuthInitializationException, AuthUnauthorizedException, BdkConfigException {
    return root().bdk();
  }

  /** Writes {@code result} to {@code stdout} as JSON and returns {@link CommandLine.ExitCode#OK}. */
  protected int emit(Object result) {
    root().emit(result);
    return CommandLine.ExitCode.OK;
  }
}
