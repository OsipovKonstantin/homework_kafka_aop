package ru.t1.java.demo.web;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class CheckClientConfig {
    private String url = "http://localhost:8088";

    private final ConnectionProvider connProvider = ConnectionProvider
            .builder("webclient-conn-pool")
            .maxConnections(80)
            .maxIdleTime(Duration.ofMillis(10))
            .maxLifeTime(Duration.ofMillis(10000))
            .pendingAcquireMaxCount(10)
            .pendingAcquireTimeout(Duration.ofMillis(40000))
            .build();

    @Bean
    public GeneralWebClient checkWebClient(ClientHttp clientHttp) {
        WebClient.Builder webClient = WebClient.builder();
        webClient
                .baseUrl(url)
                .clientConnector(clientHttp.getClientHttp(GeneralWebClient.class.getName()));
        return new GeneralWebClient(webClient.build());
    }


    @Bean
    ClientHttp getClientHttp() {
        return new ClientHttp();
    }

    public class ClientHttp {
        @SneakyThrows
        public ClientHttpConnector getClientHttp(String nameLogClass) {
            SslContext sslContext = SslContextBuilder
                    .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();

            return new ReactorClientHttpConnector(HttpClient
                    .create(connProvider)
                    .secure(t -> t.sslContext(sslContext))
                    .resolver(DefaultAddressResolverGroup.INSTANCE));
        }
    }
}
