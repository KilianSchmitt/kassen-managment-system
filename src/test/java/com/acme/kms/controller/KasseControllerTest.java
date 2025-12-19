package com.acme.kms.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;
import static com.acme.kms.controller.TestConstants.API_VERSION_INSERTER;
import static com.acme.kms.controller.TestConstants.HOST;
import static com.acme.kms.controller.TestConstants.REQUEST_FACTORY;
import static com.acme.kms.controller.TestConstants.SCHEMA;
import static com.acme.kms.config.DevConfig.DEV;
import static com.acme.kms.controller.KasseController.API_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;
import static org.junit.jupiter.api.condition.JRE.JAVA_25;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Tag("integration")
@Tag("rest")
@Tag("rest-get")
@DisplayName("REST-Schnittstelle fuer GET-Requests")
@ExtendWith(SoftAssertionsExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(DEV)
@EnabledForJreRange(min = JAVA_25, max = JAVA_25)
@SuppressWarnings({
    "WriteTag",
    "ClassFanOutComplexity",
    "MissingJavadoc",
    "MissingJavadocType",
    "JavadocVariable",
    "PMD.AtLeastOneConstructor",
    "PMD.LinguisticNaming"
})
class KasseControllerTest {
    private static final String ID_VORHANDEN_USER = "40000000-0000-0000-0000-000000000001";
    private static final String ID_VORHANDEN_02 = "40000000-0000-0000-0000-000000000002";
    private static final String ID_NICHT_VORHANDEN = "ffffffff-ffff-ffff-ffff-ffffffffffff";

    private static final String BEZEICHNUNG_VORHANDEN = "Kasse 1";
    private static final String BEZEICHNUNG_PARAM = "bezeichnung";

    private final KasseRepository kasseRepo;

    @InjectSoftAssertions
    @SuppressWarnings("NullAway.Init")
    private SoftAssertions softly;

    @SuppressFBWarnings("CT")
    KasseControllerTest(@LocalServerPort final int port, final ApplicationContext ctx) {
        final var controller = ctx.getBean(KasseController.class);
        assertThat(controller).isNotNull();

        final var uriComponents = UriComponentsBuilder.newInstance()
                .scheme(SCHEMA)
                .host(HOST)
                .port(port)
                .path(API_PATH)
                .build();
        final var baseUrl = uriComponents.toUriString();

        final var restClient = RestClient
                .builder()
                .requestFactory(REQUEST_FACTORY)
                .apiVersionInserter(API_VERSION_INSERTER)
                .baseUrl(baseUrl)
                .build();
        final var clientAdapter = RestClientAdapter.create(restClient);
        final var proxyFactory = HttpServiceProxyFactory.builderFor(clientAdapter).build();
        kasseRepo = proxyFactory.createClient(KasseRepository.class);
    }

    @Test
    @DisplayName("Immer erfolgreich")
    void immerErfolgreich() {
        assertThat(true).isTrue();
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandener Bezeichnung: teil={0}")
    @ValueSource(strings = BEZEICHNUNG_VORHANDEN)
    @DisplayName("Suche mit vorhandener Bezeichnung (Teil-String)")
    void getByBezeichnung(final String teil) {
        // given
        final var suchparameter = MultiValueMap.fromSingleValue(Map.of(BEZEICHNUNG_PARAM, teil));

        // when
        final var kassen = kasseRepo.get(suchparameter);

        // then
        assertThat(kassen.content())
                .isNotNull()
                .isNotEmpty();
        kassen.content()
                .stream()
                .map(KasseOhneKassenbons::bezeichnung)
                .forEach(bezeichnung -> softly.assertThat(bezeichnung).containsIgnoringCase(teil));
    }

    @Nested
    @DisplayName("REST-Schnittstelle fuer die Suche anhand der ID")
    class GetById {
        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID: id={0}")
        @ValueSource(strings = {ID_VORHANDEN_USER, ID_VORHANDEN_02})
        @DisplayName("Suche mit vorhandener ID")
        void getById(final String id) {
            // when
            final var response = kasseRepo.getByIdOhneVersion(id);

            // then
            final var kasse = response.getBody();
            assertThat(kasse).isNotNull();
            softly.assertThat(kasse.id()).isEqualTo(UUID.fromString(id));
            softly.assertThat(kasse.bezeichnung()).isNotNull();
        }

        @ParameterizedTest(name = "[{index}] Suche mit nicht-vorhandener ID: {0}")
        @ValueSource(strings = ID_NICHT_VORHANDEN)
        @DisplayName("Suche mit nicht-vorhandener ID")
        void getByIdNichtVorhanden(final String id) {
            // when
            final var exc = catchThrowableOfType(
                    HttpClientErrorException.NotFound.class,
                    () -> kasseRepo.getByIdOhneVersion(id)
            );

            // then
            assertThat(exc.getStatusCode()).isEqualTo(NOT_FOUND);
        }
    }
}
