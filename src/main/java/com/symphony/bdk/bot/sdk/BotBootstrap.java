package com.symphony.bdk.bot.sdk;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Triggers Spring scanning over SDK classes. Clients using the SDK must import
 * this class using Spring import.
 *
 * @author Marcus Secato
 *
 */
@Configuration
@ComponentScan("com.symphony.bdk.bot.sdk")
@PropertySource("classpath:application-base.properties")
public class BotBootstrap {

}
