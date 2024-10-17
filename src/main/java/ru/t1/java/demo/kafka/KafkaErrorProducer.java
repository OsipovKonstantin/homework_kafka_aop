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

    private final KafkaTemplate<String, ErrorDto> errorDtoTemplate;
    private final KafkaTemplate<String, Long> longTemplate;

    public void sendTo(String topic, ErrorDto errorDto) {
        try {
            errorDtoTemplate.send(topic, errorDto).get();
            errorDtoTemplate.flush();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void sendTo(String topic, Long id) {
        try {
            longTemplate.send(topic, id).get();
            longTemplate.flush();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
