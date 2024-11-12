package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.web.GeneralWebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
class ClientServiceIntegrationTest {

    @Mock
    KafkaClientProducer kafkaClientProducer;

    @MockBean
    ClientRepository clientRepository;

    //    @MockBean
    @Autowired
    GeneralWebClient generalWebClient;

    //    @InjectMocks
    @Autowired
    ClientServiceImpl clientService;

    @Test
    void registerClientTest() {

        Client client = new Client();
        client.setFirstName("John");
        client.setLastName("Doe");

        Client client2 = new Client();
        client2.setFirstName("John");
        client2.setLastName("Doe");

        when(clientRepository.save(client)).thenReturn(client2);

        doNothing()
                .when(kafkaClientProducer)
                .send(anyLong());

        List<Client> clients = clientService.registerClients(List.of(client));

        assertThat(clients.get(0).getFirstName()).isEqualTo("John");

    }

}
