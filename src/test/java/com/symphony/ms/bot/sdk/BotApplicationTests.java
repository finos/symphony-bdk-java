package com.symphony.ms.bot.sdk;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.symphony.ms.bot.sdk.command.DefaultBotMentionHandler;
import com.symphony.ms.bot.sdk.command.HelloCommandHandler;
import com.symphony.ms.bot.sdk.command.HelpCommandHandler;
import com.symphony.ms.bot.sdk.event.UserJoinedEventHandler;
import com.symphony.ms.bot.sdk.internal.command.CommandDispatcher;
import com.symphony.ms.bot.sdk.internal.command.CommandFilter;
import com.symphony.ms.bot.sdk.internal.event.EventDispatcher;
import com.symphony.ms.bot.sdk.internal.event.EventListener;
import com.symphony.ms.bot.sdk.internal.lib.templating.TemplateService;
import com.symphony.ms.bot.sdk.internal.message.MessageService;
import model.Stream;
import model.User;
import model.events.UserJoinedRoom;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BotApplicationTests {

  @Autowired
  private CommandFilter filter;

  @Autowired
  private CommandDispatcher dispatcher;

  @Autowired
  private HelloCommandHandler hello;

  @Autowired
  private HelpCommandHandler help;

  @Autowired
  private DefaultBotMentionHandler defaultCmd;

  @Autowired
  private EventDispatcher eventDispatcher;

  @Autowired
  private UserJoinedEventHandler ujeh;

  @Autowired
  private EventListener listener;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private MessageService messageService;

  @Test
  public void contextLoads() {

  }

  @Test
  public void testFilter() {
    filter.addFilter("asdsa", e -> e != null);
  }

  @Test
  public void testDispatcher() {
    dispatcher.register("asdas", hello);
  }

  /*@Test
  public void testHello() {
    hello.handle(null);
  }

  @Test
  public void testHelp() {
    help.handle(null);
  }

  @Test
  public void testDefault() {
    defaultCmd.handle(null);
  }*/

  @Test
  public void testCommandReceived() {
    User userA = new User();
    userA.setUserId(1L);
    userA.setDisplayName("John Doe");
    Stream stream = new Stream();
    stream.setStreamId("1");

    //filter.filter("@BotName ", userA, stream);
  }

  @Test
  public void testEventHandler() {
    UserJoinedRoom ev = new UserJoinedRoom();
    User us = new User();
    us.setUserId(1L);
    us.setDisplayName("john doe");
    Stream stream = new Stream();
    stream.setStreamId("1L");
    stream.setRoomName("room one");
    ev.setAffectedUser(us);
    ev.setStream(stream);

    listener.onUserJoinedRoom(ev);
  }

}
