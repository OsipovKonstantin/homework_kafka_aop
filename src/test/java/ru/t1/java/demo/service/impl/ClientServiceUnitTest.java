package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.web.GeneralWebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceUnitTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private KafkaClientProducer kafkaClientProducer;

    @Mock
    private GeneralWebClient generalWebClient;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void testRegisterClients_success() {
        Client client = new Client();
        client.setId(1L);

        List<Client> clients = List.of(client);
        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setBlocked(false);

        when(generalWebClient.check(client.getId())).thenReturn(Optional.of(checkResponse));
        when(clientRepository.save(client)).thenReturn(client);

        List<Client> savedClients = clientService.registerClients(clients);

        assertNotNull(savedClients);
        assertEquals(1, savedClients.size());
        verify(clientRepository, times(1)).save(client);
        verify(kafkaClientProducer, times(1)).send(client.getId());
    }

    @Test
    void testRegisterClients_blockedClient() {
        Client client = new Client();
        client.setId(1L);

        List<Client> clients = List.of(client);
        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setBlocked(true);

        when(generalWebClient.check(client.getId())).thenReturn(Optional.of(checkResponse));

        List<Client> savedClients = clientService.registerClients(clients);

        assertNotNull(savedClients);
        assertTrue(savedClients.isEmpty());  // Клиент не был сохранен
        verify(clientRepository, never()).save(client);  // Метод save не вызывается
        verify(kafkaClientProducer, never()).send(client.getId());
    }

    @Test
    void testRegisterClients_noCheckResponse() {
        Client client = new Client();
        client.setId(1L);

        List<Client> clients = List.of(client);

        when(generalWebClient.check(client.getId())).thenReturn(Optional.empty());

        List<Client> savedClients = clientService.registerClients(clients);

        assertNotNull(savedClients);
        assertTrue(savedClients.isEmpty());
        verify(clientRepository, never()).save(client);
        verify(kafkaClientProducer, never()).send(client.getId());
    }

    @Test
    void testParseJson_success() throws IOException {
        List<ClientDto> mockClients = new ArrayList<>();
        mockClients.add(new ClientDto());
        mockClients.add(new ClientDto());

        ClientServiceImpl spyService = spy(clientService);
        doReturn(mockClients).when(spyService).parseJson();

        List<ClientDto> clients = spyService.parseJson();

        assertNotNull(clients);
        assertEquals(2, clients.size());
    }

    @Test
    void testRegisterClient_success() {
        Client client = new Client();
        client.setId(1L);

        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setBlocked(false);

        when(generalWebClient.check(client.getId())).thenReturn(Optional.of(checkResponse));
        when(clientRepository.save(client)).thenReturn(client);

        Client savedClient = clientService.registerClient(client);

        assertNotNull(savedClient);
        verify(clientRepository, times(1)).save(client);
        verify(kafkaClientProducer, times(1)).send(client.getId());
    }

    @Test
    void testRegisterClient_blockedClient() {
        Client client = new Client();
        client.setId(1L);

        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setBlocked(true);

        when(generalWebClient.check(client.getId())).thenReturn(Optional.of(checkResponse));

        Client savedClient = clientService.registerClient(client);

        assertNull(savedClient);  // Клиент не был сохранен
        verify(clientRepository, never()).save(client);  // Метод save не вызывается
        verify(kafkaClientProducer, never()).send(client.getId());
    }

    @Test
    void testRegisterClient_noCheckResponse() {
        Client client = new Client();
        client.setId(1L);

        when(generalWebClient.check(client.getId())).thenReturn(Optional.empty());

        Client savedClient = clientService.registerClient(client);

        assertNull(savedClient);
        verify(clientRepository, never()).save(client);
        verify(kafkaClientProducer, never()).send(client.getId());
    }
}
