package com.symphony.bdk.cli;

import com.symphony.bdk.core.SymphonyBdk;

import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

/** Shared harness: runs the CLI with captured stdout/stderr and a (usually mocked) BDK facade. */
abstract class CliTestBase {

  protected StringWriter out;
  protected StringWriter err;

  /** Run with a pre-built facade injected via the test seam. */
  protected int execute(SymphonyBdk bdk, String... args) {
    final BdkCli app = new BdkCli();
    app.setBdk(bdk);
    return run(app, args);
  }

  /** Run an arbitrary (possibly overridden) root command instance. */
  protected int run(BdkCli app, String... args) {
    out = new StringWriter();
    err = new StringWriter();
    final CommandLine cmd = BdkCli.commandLine(app);
    cmd.setOut(new PrintWriter(out, true));
    cmd.setErr(new PrintWriter(err, true));
    return cmd.execute(args);
  }

  protected String stdout() {
    return out.toString();
  }

  protected String stderr() {
    return err.toString();
  }
}
