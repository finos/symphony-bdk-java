package com.symphony.bdk.spring.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.http.api.tracing.DistributedTracingContext;
import com.symphony.bdk.http.api.tracing.MDCUtils;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ErrorHandler;

@SpringBootTest(classes = RealTimeEventsAsyncTestListener.class)
@ContextConfiguration(classes = RealTimeEventListenerAsyncTest.TestContextConfig.class)
@ExtendWith(SpringExtension.class)
public class RealTimeEventListenerAsyncTest {
  @Autowired
  private ApplicationEventPublisher publisher;

  @MockitoSpyBean
  private RealTimeEventsAsyncTestListener eventListener;

  private RealTimeEventsDispatcher dispatcher;

  private V4Initiator initiator;

  static boolean hasException = false;

  @BeforeEach
  public void setUp() {
    dispatcher = new RealTimeEventsDispatcher(publisher);
    initiator = new V4Initiator();
  }

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  @SneakyThrows
  void testOnSymphonyElementsAction() {
    final V4SymphonyElementsAction payload = new V4SymphonyElementsAction();
    // Inject trace id and the current thread name
    DistributedTracingContext.setTraceId();
    MDC.put("thread_name", Thread.currentThread().getName());
    dispatcher.onSymphonyElementsAction(initiator, payload);

    verify(eventListener, timeout(100).only()).onSymphonyElementsAction(eq(new RealTimeEvent<>(initiator, payload)));
    assertThat(hasException).isFalse();
  }

  @TestConfiguration
  static class TestContextConfig {
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
      final SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
      SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
      simpleAsyncTaskExecutor.setTaskDecorator(MDCUtils::wrap);
      eventMulticaster.setTaskExecutor(simpleAsyncTaskExecutor);
      eventMulticaster.setErrorHandler(new RealTimeEventListenerAsyncTest.TestErrorHandler());
      return eventMulticaster;
    }
  }

  static class TestErrorHandler implements ErrorHandler {
    @Override
    public void handleError(Throwable throwable) {
      hasException = true;
    }
  }
}
