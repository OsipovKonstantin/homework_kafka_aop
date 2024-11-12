package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.web.GeneralWebClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final KafkaClientProducer kafkaClientProducer;
    private final GeneralWebClient generalWebClient;

    @Metric
    @Override
    public List<Client> registerClients(List<Client> clients) {
        List<Client> savedClients = new ArrayList<>();
        for (Client client : clients) {
            Optional<CheckResponse> check = generalWebClient.check(client.getId());
            check.ifPresent(checkResponse -> {
                if (!checkResponse.getBlocked()) {
                    Client saved = repository.save(client);
                    kafkaClientProducer.send(saved.getId());
                    savedClients.add(saved);
                }
            });
        }
        return savedClients;
    }

    @Metric
    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> parseJson() {
        ObjectMapper mapper = new ObjectMapper();

        ClientDto[] clients = new ClientDto[0];
        try {
            clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(clients);
    }

    @Override
    public Client registerClient(Client client) {
        Client saved = null;
        Optional<CheckResponse> check = generalWebClient.check(client.getId());
        if (check.isPresent()) {
            if (!check.get().getBlocked()) {
                saved = repository.save(client);
                kafkaClientProducer.send(client.getId());
            }
        }
        return saved;
    }
}
