package ru.t1.java.demo.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.ErrorDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaErrorProducer {

    private final KafkaTemplate<String, ErrorDto> accountDtoTemplate;

    public void sendTo(String topic, ErrorDto errorDto) {
        try {
            accountDtoTemplate.send(topic, errorDto).get();
            accountDtoTemplate.flush();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
