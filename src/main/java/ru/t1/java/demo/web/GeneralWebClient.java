package ru.t1.java.demo.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import ru.t1.java.demo.model.dto.CheckRequest;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.TransactionIsAllowedResponse;

import java.util.Optional;

@Slf4j
public class GeneralWebClient extends BaseWebClient {

    private String resource = "/bsc-wire-mock/api/client/check";
    private String transactionResource = "/bsc-wire-mock/api/transaction/isAllowed";

    public GeneralWebClient(WebClient webClient) {
        super(webClient);
    }

    public Optional<CheckResponse> check(Long id) {
        log.debug("Старт запроса с id {}", id);
        ResponseEntity<CheckResponse> post = null;
        try {
            CheckRequest request = CheckRequest.builder()
                    .clientId(id)
                    .build();

            post = this.post(
                    uriBuilder -> uriBuilder.path(resource).build(),
                    request,
                    CheckResponse.class);


        } catch (Exception httpStatusException) {
            throw httpStatusException;
        }

        log.debug("Финиш запроса с id {}", id);
        return Optional.ofNullable(post.getBody());
    }

    public Optional<TransactionIsAllowedResponse> isAllowed(Long id) {
        log.debug("Старт запроса с id {}", id);
        ResponseEntity<TransactionIsAllowedResponse> post = null;
        try {
            CheckRequest request = CheckRequest.builder()
                    .clientId(id)
                    .build();

            post = this.post(
                    uriBuilder -> uriBuilder.path(transactionResource).build(),
                    request,
                    TransactionIsAllowedResponse.class);

        } catch (Exception httpStatusException) {
            throw httpStatusException;
        }

        log.debug("Финиш запроса с id {}", id);
        return Optional.ofNullable(post.getBody());
    }
}