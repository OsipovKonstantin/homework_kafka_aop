package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaMetricProducer;
import ru.t1.java.demo.model.dto.MetricDto;

import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {
    private final KafkaMetricProducer kafkaMetricProducer;
    @Value("${t1.kafka.topic.metric}")
    private String topic;

    @Value("${t1.aop.execution-time-threshold}")
    private long executionTimeThreshold;

    private static final AtomicLong START_TIME = new AtomicLong();

    @Before("@annotation(ru.t1.java.demo.aop.Metric)")
    public void initialTime() throws Throwable {
        START_TIME.addAndGet(System.currentTimeMillis());
    }

    @After("@annotation(ru.t1.java.demo.aop.Metric)")
    public void processDuration(JoinPoint joinPoint) throws Throwable {
        long endTime = System.currentTimeMillis();
        long duration = endTime - START_TIME.get();
        if (duration > executionTimeThreshold) {
            kafkaMetricProducer.sendTo(topic, MetricDto.builder()
                    .methodName(joinPoint.getSignature().toShortString())
                    .methodParams(joinPoint.getArgs())
                    .executionTime(duration)
                    .build());
        }
        START_TIME.set(0L);
    }
}
