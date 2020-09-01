package com.symphony.bdk.examples.command;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.config.BdkConfigLoader;

/**
 * TODO: add description here
 */
public class CommandMain {

  public static void main(String[] args) throws Exception {

    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(BdkConfigLoader.loadFromClasspath("/config.yaml"));

    // register the GifCommand
    bdk.commands().register(new GifCommand());

    // register form reply command
    bdk.commands().register(new OnFormReplyCommand());

    // start the Datafeed loop
    bdk.datafeed().start();
  }
}
