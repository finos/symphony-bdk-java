package com.symphony.bdk.bot.sdk.lib.jsonmapper.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.symphony.bdk.bot.sdk.lib.jsonmapper.JsonMapper;
import com.symphony.bdk.bot.sdk.lib.jsonmapper.JsonMapperImpl;

/**
 * Creates and configures an instance of the Jackson-based implementation of
 * the {@link JsonMapper} if no other implementation is provided.
 *
 * @author Marcus Secato
 *
 */
@Configuration
public class JsonMapperConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapperConfig.class);

  @Bean
  //@ConditionalOnProperty(value = "jsonmapper.disabled", havingValue="false", matchIfMissing=true)
  @ConditionalOnMissingBean
  public JsonMapper jsonMapper() {
    LOGGER.info("Initializing JSON mapper");

    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.setSerializationInclusion(Include.NON_EMPTY);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    return new JsonMapperImpl(mapper);
  }

}
