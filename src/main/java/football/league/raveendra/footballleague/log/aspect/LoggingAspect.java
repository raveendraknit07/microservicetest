package football.league.raveendra.footballleague.log.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import football.league.raveendra.footballleague.log.annotation.Trace;

/***
 * This class is a spring aspect, which defines a point cut All public methods which are annotated
 * with Trace are surrounded by the log() method, which is responsible for logging details of method
 * and execution times, this data is used for auditing purpose.
 */
@Slf4j
@Component
@Aspect
public class LoggingAspect {

  /***
   * @param joinPoint
   * @param trace
   * @return
   * @throws Throwable This method wraps all methods that are annotated with com.example.Trace
   *         annotation, Trace annotation has 1 attribute com.example.LogEventType(tells whether its
   *         a request/service/db call) For each request this logs method name , time taken and type
   *         of call for every method that is called during request processing and that meet above
   *         criteria.
   */
  @Around("@annotation(trace)")
  public Object log(ProceedingJoinPoint joinPoint, Trace trace) throws Throwable {
    long startTime = 0;
    try {
      log.info(
          "[" + trace.type() + "] Execution started : " + joinPoint.getSignature().toShortString());
      startTime = System.currentTimeMillis();
      return joinPoint.proceed();
    } catch (Exception e) {
      log.info("[" + trace.type() + "] Exception occurred while executing "
          + joinPoint.getSignature().toShortString() + ".Rethrowing exception " + e.getMessage());
      throw e;
    } finally {
      log.info(
          "[" + trace.type() + "] Execution completed : " + joinPoint.getSignature().toShortString()
              + " , ExecutionTime(in millis) :" + (System.currentTimeMillis() - startTime));
    }
  }


}
