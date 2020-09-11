package com.symphony.bdk.bot.sdk.symphony;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import clients.SymBotClient;
import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;
import listeners.ConnectionListener;

@Service
public class DatafeedClientImpl implements DatafeedClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatafeedClientImpl.class);

  private SymBotClient symBotClient;

  public DatafeedClientImpl(SymBotClient symBotClient) {
    this.symBotClient = symBotClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerIMListener(IMListener imListener) {
    LOGGER.info("Adding IM listener");
    symBotClient.getDatafeedEventsService().addIMListener(imListener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerRoomListener(RoomListener roomListener) {
    LOGGER.info("Adding Room listener");
    symBotClient.getDatafeedEventsService().addRoomListener(roomListener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerElementsListener(ElementsListener elementsListener) {
    LOGGER.info("Adding Elements listener");
    symBotClient.getDatafeedEventsService().addElementsListener(
        elementsListener);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void registerConnectionsListener(ConnectionListener connectionsListener) {
    LOGGER.info("Adding Connections listener");
    symBotClient.getDatafeedEventsService().addConnectionsListener(connectionsListener);
  }

}
