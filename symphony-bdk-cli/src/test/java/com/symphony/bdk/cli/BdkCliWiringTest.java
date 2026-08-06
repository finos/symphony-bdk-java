package com.symphony.bdk.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.nio.file.Paths;
import java.util.Map;

/** Verifies the picocli command tree, option parsing and default config resolution. */
class BdkCliWiringTest {

  @Test
  void registersAllNounCommands() {
    final Map<String, CommandLine> subs = BdkCli.commandLine(new BdkCli()).getSubcommands();
    assertThat(subs.keySet())
        .contains("whoami", "message", "stream", "user", "datafeed", "health");
  }

  @Test
  void registersVerbSubcommands() {
    final Map<String, CommandLine> subs = BdkCli.commandLine(new BdkCli()).getSubcommands();
    assertThat(subs.get("message").getSubcommands().keySet()).contains("send", "get", "list");
    assertThat(subs.get("stream").getSubcommands().keySet()).contains("list", "members", "get");
    assertThat(subs.get("user").getSubcommands().keySet()).contains("get", "search");
    assertThat(subs.get("datafeed").getSubcommands().keySet()).contains("read");
    assertThat(subs.get("health").getSubcommands().keySet()).contains("check");
  }

  @Test
  void defaultConfigPathPointsAtSymphonyDir() {
    final BdkCli app = new BdkCli();
    BdkCli.commandLine(app).parseArgs("whoami");
    assertThat(app.configPath.toString()).endsWith(".symphony/config.yaml");
    assertThat(app.configPath.toString()).startsWith(System.getProperty("user.home"));
  }

  @Test
  void parsesGlobalOptions() {
    final BdkCli app = new BdkCli();
    BdkCli.commandLine(app).parseArgs("-c", "/tmp/other.yaml", "-v", "whoami");
    assertThat(app.configPath).isEqualTo(Paths.get("/tmp/other.yaml"));
    assertThat(app.verbose).isTrue();
  }

  @Test
  void longFormOptionsParse() {
    final BdkCli app = new BdkCli();
    BdkCli.commandLine(app).parseArgs("--config", "/tmp/x.yaml", "--verbose", "whoami");
    assertThat(app.configPath).isEqualTo(Paths.get("/tmp/x.yaml"));
    assertThat(app.verbose).isTrue();
  }
}
