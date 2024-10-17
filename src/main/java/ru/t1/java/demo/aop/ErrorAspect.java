package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.exception.TransactionError;
import ru.t1.java.demo.kafka.KafkaErrorProducer;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.ErrorDto;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class ErrorAspect {
    private final KafkaErrorProducer kafkaErrorProducer;
    @Value("${t1.kafka.topic.error}")
    private String topic;

    @Value("${t1.kafka.topic.transaction_error}")
    private String transactionErrorTopic;

    @Pointcut("@annotation(ru.t1.java.demo.aop.TransactionKafkaLog)")
    public void loggingMethod() {
    }

    @AfterThrowing(value = "loggingMethod()", throwing = "ex")
    public void log(JoinPoint joinPoint, Throwable ex) {
        kafkaErrorProducer.sendTo(topic, ErrorDto.builder()
                .methodName(joinPoint.getSignature().toShortString())
                .methodParams(joinPoint.getArgs())
                .stackTrace(Arrays.toString(ex.getStackTrace()))
                .build());
    }
}
