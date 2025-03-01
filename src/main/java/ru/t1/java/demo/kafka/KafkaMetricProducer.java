package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.MetricDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMetricProducer {
    private final KafkaTemplate<String, MetricDto> metricDtoTemplate;

    public void sendTo(String topic, MetricDto metricDto) {
        try {
            metricDtoTemplate.send(topic, metricDto).get();
            metricDtoTemplate.flush();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
