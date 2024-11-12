package ru.t1.java.demo.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.TransactionIsAllowedResponse;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.yml")
class GeneralWebClientTest {

    CheckClientConfig config = new CheckClientConfig();
    CheckClientConfig.ClientHttp clientHttp = config.new ClientHttp();
    ClientHttpConnector connector = clientHttp.getClientHttp(GeneralWebClient.class.getName());
    GeneralWebClient generalWebClient = new GeneralWebClient(WebClient.builder()
            .baseUrl("http://localhost:8088")
            .clientConnector(connector).build());

    @Test
    void check() {
        assertThat(generalWebClient.check(1L)).get().isEqualTo(CheckResponse.builder()
                .blocked(false)
                .build());
    }

    @Test
    void isAllowed() {
        assertThat(generalWebClient.isAllowed(1L)).get().isEqualTo(TransactionIsAllowedResponse.builder()
                .isAllowed(true)
                .build());
    }
}