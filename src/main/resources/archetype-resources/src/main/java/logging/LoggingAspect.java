package ${package}.logging;

import model.InboundMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;

import java.util.UUID;

@Aspect
public class LoggingAspect {

  @Around("@annotation(mdc)")
  public void addMDCContext(ProceedingJoinPoint joinPoint, MDCContext mdc) throws Throwable {
    InboundMessage inboundMessage = (InboundMessage) joinPoint.getArgs()[0];
    MDC.put("TransactionId", String.valueOf(UUID.randomUUID()));
    MDC.put("StreamId", inboundMessage.getStream().getStreamId());
    MDC.put("UserId", String.valueOf(inboundMessage.getUser().getUserId()));
    joinPoint.proceed();
    MDC.clear();
  }
}
