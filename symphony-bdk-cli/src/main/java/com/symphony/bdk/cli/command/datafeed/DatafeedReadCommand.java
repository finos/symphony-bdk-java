package com.symphony.bdk.cli.command.datafeed;

import com.symphony.bdk.cli.command.BaseCommand;
import com.symphony.bdk.cli.internal.DurationConverter;
import com.symphony.bdk.cli.internal.Json;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4Event;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.PrintWriter;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code bdk datafeed read} — print each real-time event as a single JSON object on its own line
 * (NDJSON / JSON Lines). The loop stops after {@code --count} events, after {@code --timeout}, or on
 * SIGINT, always shutting the datafeed loop down cleanly before the process exits.
 */
@Command(
    name = "read",
    description = "Stream real-time events as JSON Lines until a bound or interruption.")
public class DatafeedReadCommand extends BaseCommand {

  @Option(names = "--count", paramLabel = "<N>", description = "Stop after N events.")
  Integer count;

  @Option(
      names = "--timeout",
      paramLabel = "<duration>",
      converter = DurationConverter.class,
      description = "Stop after this duration (e.g. 30s, 5m, 1h).")
  Duration timeout;

  @Override
  public Integer call() throws Exception {
    final DatafeedLoop loop = bdk().datafeed();
    final PrintWriter out = spec.commandLine().getOut();
    final AtomicInteger emitted = new AtomicInteger();

    loop.subscribe(new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        synchronized (out) {
          out.println(Json.compact(event));
          out.flush();
        }
        if (count != null && emitted.incrementAndGet() >= count) {
          loop.stop();
        }
        return true;
      }
    });

    // SIGINT (Ctrl-C) → stop the loop cleanly
    final Thread shutdownHook = new Thread(loop::stop, "bdk-datafeed-shutdown");
    Runtime.getRuntime().addShutdownHook(shutdownHook);

    // optional time bound
    ScheduledExecutorService scheduler = null;
    if (timeout != null) {
      scheduler = Executors.newSingleThreadScheduledExecutor();
      scheduler.schedule(loop::stop, timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    try {
      loop.start(); // blocks until stop() is called
    } finally {
      if (scheduler != null) {
        scheduler.shutdownNow();
      }
      try {
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
      } catch (IllegalStateException alreadyShuttingDown) {
        // JVM is already shutting down (SIGINT path); nothing to remove
      }
    }
    return CommandLine.ExitCode.OK;
  }
}
