package test;

import clients.SymBotClient;
import listeners.IMListener;
import model.InboundMessage;
import model.Stream;

public class BotMain {

  public static void main(String[] args) throws Exception {

    final SymBotClient botClient = SymBotClient.initBotLoadBalancedRsa(
        "config-sup.json",
        "config-sup-lb.json"
    );
    botClient.getDatafeedEventsService().addListeners(new IMListenerImpl());
  }
}

class IMListenerImpl implements IMListener {

  public void onIMMessage(InboundMessage inboundMessage) {
    System.out.println("Hello, on IM message!");
  }

  public void onIMCreated(Stream stream) {
    System.out.println("Hello, on IM created!");
  }
}