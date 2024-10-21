package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;

import java.util.List;

public interface ClientService {
    List<Client> registerClients(List<Client> clients);

    List<ClientDto> parseJson();

    Client registerClient(Client client);
}
