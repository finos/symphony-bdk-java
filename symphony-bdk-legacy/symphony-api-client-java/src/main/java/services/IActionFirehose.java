package services;

import clients.symphony.api.FirehoseClient;
import listeners.FirehoseListener;
import model.DatafeedEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IActionFirehose {
  CompletableFuture<List<DatafeedEvent>> actionHandleEvents(List<DatafeedEvent> events,List<FirehoseListener> listeners);

  List<DatafeedEvent> actionReadFirehose(FirehoseClient firehoseClient,String firehoseId);
}
