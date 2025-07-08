package com.credit_card_bill_tracker.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.credit_card_bill_tracker.backend..*Service.*(..))")
    public void logServiceMethodEntry(JoinPoint joinPoint) {
        log.info("Entering {} with args {}", joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.credit_card_bill_tracker.backend..*Service.*(..))", returning = "result")
    public void logServiceMethodExit(JoinPoint joinPoint, Object result) {
        log.info("Exiting {} with result {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.credit_card_bill_tracker.backend..*Service.*(..))", throwing = "ex")
    public void logServiceException(JoinPoint joinPoint, Throwable ex) {
        log.error("Exception in {} with cause {}", joinPoint.getSignature(), ex.getMessage(), ex);
    }
}
