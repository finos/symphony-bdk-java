package com.symphony.bdk.cli;

import com.symphony.bdk.cli.command.WhoamiCommand;
import com.symphony.bdk.cli.command.datafeed.DatafeedCommand;
import com.symphony.bdk.cli.command.health.HealthCommand;
import com.symphony.bdk.cli.command.message.MessageCommand;
import com.symphony.bdk.cli.command.stream.StreamCommand;
import com.symphony.bdk.cli.command.user.UserCommand;
import com.symphony.bdk.cli.internal.BdkCliExecutionExceptionHandler;
import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;

import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.nio.file.Path;

/**
 * Root command of the Symphony BDK CLI.
 *
 * <p>Loads a {@link BdkConfig} (default {@code ~/.symphony/config.yaml}, overridable with {@code
 * -c/--config}) and builds a single {@link SymphonyBdk} per invocation, acting as the configured
 * bot. Results are serialised as JSON on {@code stdout}; all logging is routed to {@code stderr}.
 */
@Command(
    name = "bdk",
    mixinStandardHelpOptions = true,
    versionProvider = BdkCli.VersionProvider.class,
    synopsisSubcommandLabel = "<command>",
    description = "Operational CLI for the Symphony BDK. Authenticates as the configured bot and "
        + "performs one-shot Symphony operations, emitting JSON on stdout.",
    subcommands = {
        WhoamiCommand.class,
        MessageCommand.class,
        StreamCommand.class,
        UserCommand.class,
        DatafeedCommand.class,
        HealthCommand.class
    })
public class BdkCli implements Runnable {

  @Spec
  CommandSpec spec;

  @Option(
      names = {"-c", "--config"},
      paramLabel = "<path>",
      defaultValue = "${sys:user.home}/.symphony/config.yaml",
      description = "Path to the bdk-config.yaml (default: ${DEFAULT-VALUE}).")
  Path configPath;

  @Option(
      names = {"-v", "--verbose"},
      description = "Raise the log level on stderr (WARN → DEBUG).")
  boolean verbose;

  /** Lazily-built BDK facade; may be pre-set for testing. */
  private SymphonyBdk bdk;

  /**
   * Builds (and caches) the {@link SymphonyBdk} facade for this invocation, authenticating as the
   * configured bot. Reachable by leaf commands through the parent-command chain.
   */
  public SymphonyBdk bdk() throws AuthInitializationException, AuthUnauthorizedException, BdkConfigException {
    applyVerbosity();
    if (bdk == null) {
      final BdkConfig config = BdkConfigLoader.loadFromFile(configPath.toString());
      bdk = SymphonyBdk.builder().config(config).build();
    }
    return bdk;
  }

  /** Test seam: inject a pre-built (typically mocked) facade, bypassing config loading and auth. */
  void setBdk(SymphonyBdk bdk) {
    this.bdk = bdk;
  }

  /** Writes a command result to {@code stdout} as a pretty-printed JSON document. */
  public void emit(Object result) {
    spec.commandLine().getOut().println(com.symphony.bdk.cli.internal.Json.pretty(result));
    spec.commandLine().getOut().flush();
  }

  private void applyVerbosity() {
    if (verbose) {
      final org.slf4j.Logger root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
      if (root instanceof ch.qos.logback.classic.Logger) {
        ((ch.qos.logback.classic.Logger) root).setLevel(ch.qos.logback.classic.Level.DEBUG);
      }
    }
  }

  /** Invoked when no subcommand is supplied: print usage and perform no Symphony call. */
  @Override
  public void run() {
    spec.commandLine().usage(spec.commandLine().getOut());
  }

  /** Builds a fully-configured {@link CommandLine} (shared by {@link #main(String[])} and tests). */
  public static CommandLine commandLine(BdkCli app) {
    final CommandLine cmd = new CommandLine(app)
        .setExecutionExceptionHandler(new BdkCliExecutionExceptionHandler())
        .setCaseInsensitiveEnumValuesAllowed(true);
    applyUsageExitCode(cmd);
    return cmd;
  }

  /** Recursively sets the invalid-input exit code on every command (see {@code EXIT_USAGE}). */
  private static void applyUsageExitCode(CommandLine cmd) {
    cmd.getCommandSpec().exitCodeOnInvalidInput(BdkCliExecutionExceptionHandler.EXIT_USAGE);
    for (CommandLine sub : cmd.getSubcommands().values()) {
      applyUsageExitCode(sub);
    }
  }

  public static void main(String[] args) {
    System.exit(commandLine(new BdkCli()).execute(args));
  }

  /** Reports the BDK version stamped into the jar manifest by the build. */
  static class VersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() {
      final String version = BdkCli.class.getPackage().getImplementationVersion();
      return new String[] {"bdk " + (version != null ? version : "(development)")};
    }
  }
}
