//package com.acme.kms.controller;
//
//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
//import org.springframework.context.ApplicationContext;
//import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.condition.EnabledForJreRange;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestClient;
//import org.springframework.web.client.support.RestClientAdapter;
//import org.springframework.web.service.invoker.HttpServiceProxyFactory;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//import static com.acme.kms.config.DevConfig.DEV;
//import static com.acme.kms.controller.TestConstants.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.catchException;
//import static org.junit.jupiter.api.condition.JRE.JAVA_25;
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
//import static org.springframework.http.HttpStatus.NO_CONTENT;
//
//@Tag("integration")
//@Tag("rest")
//@Tag("rest-write")
//@DisplayName("REST-Schnittstelle fuer Schreiben testen")
//@ExtendWith(SoftAssertionsExtension.class)
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@ActiveProfiles(DEV)
//@EnabledForJreRange(min = JAVA_25, max = JAVA_25)
//public class KasseWriteControllerTest {
//    private final KasseApiClient kasseApiClient;
//
//    @SuppressFBWarnings("CT")
//    KasseWriteControllerTest(@LocalServerPort final int port, final ApplicationContext ctx) {
//        assertThat(ctx).isNotNull();
//        final var writeController = ctx.getBean(KasseWriteController.class);
//        assertThat(writeController).isNotNull();
//
//        final var uriComponents = UriComponentsBuilder.newInstance()
//                .scheme(TestConstants.SCHEMA)
//                .host(TestConstants.HOST)
//                .port(port)
//                .path("/kassen")
//                .build();
//        final var baseUrl = uriComponents.toUriString();
//        final var restClient = RestClient
//                .builder()
//                .baseUrl(baseUrl)
//                .requestFactory(REQUEST_FACTORY)
//                .build();
//        final var clientAdapter = RestClientAdapter.create(restClient);
//        final var proxyFactory = HttpServiceProxyFactory.builderFor(clientAdapter).build();
//        kasseApiClient = proxyFactory.createClient(KasseApiClient.class);
//    }
//
//    @Nested
//    @DisplayName("Ändern")
//    class Updaten {
//        @ParameterizedTest(name = "[{index}] Ändern einer vorhandenen Kasse: id={0}")
//        @ValueSource(strings = "00000000-0000-0000-0000-000000000003")
//        @DisplayName("Ändern einer vorhandenen Kasse")
//        void update(final String id) {
//            // given
//            KassiererDTO kassierer = new KassiererDTO(
//                    "Kilian",
//                    "Schmitt",
//                    "Kilian@acme.com"
//            );
//
//            List<KassenBonDTO> kassenBons = List.of(
//                new KassenBonDTO(
//                        LocalDate.of(2025, 1, 1),
//                        BigDecimal.valueOf(99.99)
//                )
//            );
//
//            KasseDTO kasseDTO = new KasseDTO(
//                    "Kasse-Updated",
//                    kassierer,
//                    BigDecimal.valueOf(500.00),
//                    kassenBons
//            );
//
//            // when
//            final var response = kasseApiClient.put(id, kasseDTO);
//
//            // then
//            assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
//        }
//    }
//
//    @Nested
//    @DisplayName("Loeschen")
//    class Loeschen {
//        @ParameterizedTest(name = "[{index}] Loeschen einer vorhandenen Kasse: id={0}")
//        @ValueSource(strings = "00000000-0000-0000-0000-000000000001")
//        @DisplayName("Loeschen einer vorhandenen Kasse")
//        void delete(final String id) {
//            // when
//            final var response = kasseApiClient.delete(id);
//
//            // then
//            assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
//        }
//
//        @ParameterizedTest(name = "[{index}] Loeschen einer nicht vorhandenen Kasse: id={0}")
//        @ValueSource(strings = "00000000-0000-0000-0000-000000000999")
//        @DisplayName("Loeschen einer nicht vorhandenen Kasse")
//        void deleteNotFound(final String id) {
//            // when
//            Exception exception = catchException(() -> kasseApiClient.delete(id));
//
//            // then
//            assertThat(exception).isInstanceOf(HttpClientErrorException.NotFound.class);
//        }
//    }
//
//    @Nested
//    @DisplayName("Anlegen")
//    class Anlegen {
//        @ParameterizedTest(name = "[{index}] Anlegen einer neuen Kasse")
//        @ValueSource(strings = "Kasse-New")
//        @DisplayName("Kasse erfolgreich anlegen")
//        void create(final String kassenName) {
//            // given
//            KassiererDTO kassierer = new KassiererDTO(
//                    "Max",
//                    "Mustermann",
//                    "max@acme.com"
//            );
//            List<KassenBonDTO> kassenBons = List.of(
//                    new KassenBonDTO(
//                            LocalDate.of(2025, 2, 2),
//                            BigDecimal.valueOf(123.45)
//                    )
//            );
//            KasseDTO kasseDTO = new KasseDTO(
//                    kassenName,
//                    kassierer,
//                    BigDecimal.valueOf(1000.00),
//                    kassenBons
//            );
//
//            // when
//            var response = kasseApiClient.post(kasseDTO);
//
//            // then
//            assertThat(response.getStatusCode().value()).isEqualTo(201);
//            assertThat(response.getHeaders().getLocation()).isNotNull();
//            assertThat(response.getHeaders().getLocation().toString()).contains("/kassen/");
//        }
//    }
//
//}
//
