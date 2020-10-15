package services;

import clients.SymBotClient;
import clients.symphony.api.FirehoseClient;
import exceptions.SymClientException;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import listeners.FirehoseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirehoseService {
  private final Logger logger = LoggerFactory.getLogger(FirehoseService.class);
  private SymBotClient botClient;
  private FirehoseClient firehoseClient;
  private List<FirehoseListener> listeners;
  private String firehoseId;
  private ExecutorService pool;
  private AtomicBoolean stop = new AtomicBoolean();
  private ActionFirehose action;

  public FirehoseService(SymBotClient client) {
    this(client, client.getFirehoseClient().createFirehose());
  }

  public FirehoseService(SymBotClient client, String firehoseId) {
    this(client, firehoseId, new ActionFirehoseImpl(client));
  }

  protected FirehoseService(SymBotClient client, String firehoseId, ActionFirehose actionFireHose) {
    this.botClient = client;
    listeners = new ArrayList<>();
    firehoseClient = this.botClient.getFirehoseClient();
    this.firehoseId = firehoseId;
    this.action = actionFireHose;

    readFirehose();
    stop.set(false);
  }

  public void addListener(FirehoseListener listener) {
    listeners.add(listener);
  }

  public void removeListener(FirehoseListener listener) {
    listeners.remove(listener);
  }

  public void readFirehose() {
    if (pool != null) {
      pool.shutdown();
    }
    pool = Executors.newFixedThreadPool(5);
    Executors.newSingleThreadExecutor().submit(() -> {
      while (!stop.get()) {
        CompletableFuture<Object> future = getFirehoseHandleEventFuture(this.action, pool);
        try {
          future.get();
        } catch (InterruptedException | ExecutionException e) {
          logger.error("Error trying to read firehose ", e);
        }
      }
      return null;
    });
  }

  protected CompletableFuture<Object> getFirehoseHandleEventFuture(ActionFirehose action, Executor pool) {
    return CompletableFuture.supplyAsync(() -> action.actionReadFirehose(this.firehoseClient, this.firehoseId), pool)
      .exceptionally((ex) -> {
        handleError(ex);
        return null;
      })
      .thenApply(events -> action.actionHandleEvents(events, listeners));
  }

  public void stopDatafeedService() {
    if (!stop.get()) {
      stop.set(true);
    }
  }

  public void restartDatafeedService() {
    if (stop.get()) {
      stop.set(false);
    }
    firehoseId = firehoseClient.createFirehose();
    readFirehose();
  }

  private void handleError(Throwable e) {
    Sleeper sleeper = new Sleeper();
    logger.error(e.getMessage());
    sleeper.sleep(30);
    try {
      firehoseId = firehoseClient.createFirehose();
    } catch (SymClientException e1) {
      sleeper.sleep(30);
      handleError(e);
    }
  }
}
