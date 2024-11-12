package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.datafeed.DatahoseLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.impl.DatahoseLoopImpl;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;
import com.symphony.bdk.spring.service.BotInfoService;
import com.symphony.bdk.spring.service.DatahoseAsyncLauncherService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.List;

@ConditionalOnProperty(value = "bdk.datahose.enabled", havingValue = "true")
@ConditionalOnBean(name = "botSession")
public class BdkDatahoseConfig {

  @Bean("datahoseLoop")
  @ConditionalOnMissingBean
  public DatahoseLoop datahoseLoop(SymphonyBdkCoreProperties properties,
                                       @Qualifier("datahoseApi") DatafeedApi datafeedApi,
                                       AuthSession botSession,
                                       BotInfoService botInfoService) {
    return new DatahoseLoopImpl(datafeedApi, botSession, properties, botInfoService.getBotInfo(), null);
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public DatahoseAsyncLauncherService datahoseAsyncLauncherService(@Qualifier("datahoseLoop") DatahoseLoop datahoseService,
      List<RealTimeEventListener> realTimeEventListeners) {
    return new DatahoseAsyncLauncherService(datahoseService, realTimeEventListeners);
  }
}
