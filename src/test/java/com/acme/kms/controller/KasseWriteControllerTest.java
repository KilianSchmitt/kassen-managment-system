package com.acme.kms.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static com.acme.kms.config.DevConfig.DEV;
import static com.acme.kms.controller.KasseController.API_PATH;
import static com.acme.kms.controller.KasseController.ID_PATTERN;
import static com.acme.kms.controller.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.JRE.JAVA_25;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;

@Tag("integration")
@Tag("rest")
@Tag("rest-write")
@DisplayName("REST-Schnittstelle fuer Schreiben")
@ExtendWith(SoftAssertionsExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(DEV)
@EnabledForJreRange(min = JAVA_25, max = JAVA_25)
@SuppressWarnings("PMD")
public class KasseWriteControllerTest {
    private static final String ID_UPDATE_PUT = "40000000-0000-0000-0000-000000000001";
    private static final String ID_DELETE = "40000000-0000-0000-0000-000000000002";
    private static final String ID_NICHT_VORHANDEN = "ffffffff-ffff-ffff-ffff-ffffffffffff";

    private static final String KASSEN_BEZEICHNUNG = "Kasse 99";
    private static final String KASSEN_BARGELDBESTAND = "1000.00";

    private final KasseRepository kasseRepo;

    @InjectSoftAssertions
    @SuppressWarnings("NullAway.Init")
    private SoftAssertions softly;

    @SuppressFBWarnings("CT")
    KasseWriteControllerTest(@LocalServerPort final int port, final ApplicationContext ctx) {
        final var writeController = ctx.getBean(KasseWriteController.class);
        assertThat(writeController).isNotNull();

        final var uriComponents = UriComponentsBuilder.newInstance()
                .scheme(SCHEMA)
                .host(HOST)
                .port(port)
                .path(API_PATH)
                .build();
        final var baseUrl = uriComponents.toUriString();
        // RestClient mit REQUEST_FACTORY aus KundeControllerTest.java einschl. TLS bzw. SSLContext
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

    @Nested
    @DisplayName("REST-Schnittstelle fuer POST")
    class Erzeugen {
        @ParameterizedTest(name = "[{index}] Neuanlegen einer neuen Kasse mit Bezeichnung={0}, Bargeldbestand={1}")
        @CsvSource(
                KASSEN_BEZEICHNUNG + "," + KASSEN_BARGELDBESTAND
        )
        @DisplayName("Neuanlegen einer neuen Kasse")
        @SuppressWarnings("BooleanExpressionComplexity")
        void create(final ArgumentsAccessor args) {
            // given
            final var bezeichnung = args.getString(0);
            final var bargeldbestand = args.getString(1);
            if (bezeichnung == null || bargeldbestand == null) {
                throw new IllegalStateException("Testdaten sind null");
            }

            final var kasseDTO = new KasseDTO(
                    bezeichnung,
                    null,
                    new BigDecimal(bargeldbestand),
                    null
            );

            // when
            final var response = kasseRepo.post(kasseDTO);

            // then
            softly.assertThat(response.getStatusCode()).isEqualTo(CREATED);
            final var location = response.getHeaders().getLocation();
            assertThat(location)
                    .isNotNull()
                    .isInstanceOf(URI.class);
            assertThat(location.toString()).matches(".*/" + ID_PATTERN + '$');
        }

        @ParameterizedTest(name = "[{index}] Neuanlegen mit ungültigen Werten: bezeichnung={0}, bargeldbestand={1}")
        @CsvSource("-1000.00")
        @DisplayName("Neuanlegen mit ungültigen Werten")
        void createInvalid(final ArgumentsAccessor args) {
            // given
            final var bargeldbestand = args.getString(0);
            if (bargeldbestand == null) {
                throw new IllegalStateException("Testdaten sind null");
            }

            final var kasseDTO = new KasseDTO(
                    "Kasse Invalid",
                    null,
                    new BigDecimal(bargeldbestand),
                    null
            );
            final var violationKeys = List.of("bargeldbestand");

            // when
            final var exc = catchThrowableOfType(
                    HttpClientErrorException.UnprocessableContent.class,
                    () -> kasseRepo.post(kasseDTO)
            );

            // then
            softly.assertThat(exc.getStatusCode()).isEqualTo(UNPROCESSABLE_CONTENT);
            final var body = exc.getResponseBodyAs(ProblemDetail.class);
            softly.assertThat(body).isNotNull();

            if (body == null) {
                throw new IllegalStateException("ProblemDetail ist null");
            }

            final var detail = body.getDetail();
            softly.assertThat(detail).isNotNull();

            if (detail == null) {
                throw new IllegalStateException("Detail ist null");
            }

            final var violations = detail.split(", ");
            final var actualViolationKeys = Arrays.stream(violations)
                    .map(violation -> {
                        final var colonIndex = violation.indexOf(": ");
                        return colonIndex > 0 ? violation.substring(0, colonIndex) : violation;
                    })
                    .toList();
            softly.assertThat(actualViolationKeys).containsExactlyInAnyOrderElementsOf(violationKeys);
        }
    }

    @Nested
    @DisplayName("REST-Schnittstelle fuer Aendern")
    class Aendern {
        @Nested
        @DisplayName("REST-Schnittstelle fuer Put")
        class AendernDurchPut {
            @ParameterizedTest(name = "[{index}] Aendern einer vorhandenen Kasse mit id: id={0}")
            @ValueSource(strings = ID_UPDATE_PUT)
            @DisplayName("Aendern einer vorhandenen Kasse durch Put")
            void put(final String id) {
                // given
                final var responseGet = kasseRepo.getByIdOhneVersion(id);
                var etag = responseGet.getHeaders().getETag();
                if (etag == null) {
                    etag = "\"0\"";
                }
                final var kasseOrig = responseGet.getBody();
                assertThat(kasseOrig).isNotNull();

                final var kasse = new KasseDTO(
                        kasseOrig.bezeichnung(),
                        kasseOrig.kassierer(),
                        kasseOrig.betrag(),
                        null
                );

                // when
                final var response = kasseRepo.put(id, kasse, etag);

                // then
                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @ParameterizedTest(name = "[{index}] Aendern durch Put mit ungueltigen Werten: id={0}, nachname={1}")
        @ValueSource(strings = ID_UPDATE_PUT)
        @DisplayName("Aendern durch Put mit ungueltigen Werten")
        void updateInvalid(final String id) {
            // given
            final var responseGet = kasseRepo.getByIdOhneVersion(id);
            final var etag = responseGet.getHeaders().getETag();
            final var kasseOrig = responseGet.getBody();
            assertThat(kasseOrig).isNotNull();

            final var kasse = new KasseDTO(
                    kasseOrig.bezeichnung(),
                    kasseOrig.kassierer(),
                    new BigDecimal(-500.00),
                    null
            );
            final var violationKeys = List.of("bargeldbestand");

            // when
            final var exc = catchThrowableOfType(
                    HttpClientErrorException.UnprocessableContent.class,
                    () -> kasseRepo.put(id, kasse, etag == null ? "\"0\"" : etag)
            );

            // then
            assertThat(exc.getStatusCode()).isEqualTo(UNPROCESSABLE_CONTENT);
            final var body = exc.getResponseBodyAs(ProblemDetail.class);
            assertThat(body).isNotNull();
            final var detail = body.getDetail();
            assertThat(detail)
                    .isNotNull()
                    .isNotEmpty();
            @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
            final var violations = detail.split(", ");
            assertThat(violations)
                    .isNotNull()
                    .hasSize(violationKeys.size());
            final var actualViolationKeys = Arrays.stream(violations)
                    .map(violation -> violation.substring(0, violation.indexOf(": ")))
                    .toList();
            assertThat(actualViolationKeys).containsExactlyInAnyOrderElementsOf(violationKeys);
        }
    }

    @Nested
    @DisplayName("REST-Schnittstelle fuer DELETE")
    class Loeschen {
        @ParameterizedTest(name = "[{index}] Loeschen einer vorhandenen Kasse: id={0}")
        @ValueSource(strings = ID_DELETE)
        @DisplayName("Loeschen einer vorhandenen Kasse")
        void deleteById(final String id) {
            // when
            final var response = kasseRepo.deleteById(id);

            // then
            assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        }

        @ParameterizedTest(name = "[{index}] Loeschen einers nicht-vorhandenen Kasse: id={0}")
        @ValueSource(strings = ID_NICHT_VORHANDEN)
        @DisplayName("Loeschen einers nicht-vorhandenen Kasse")
        void deleteByIdNichtVorhanden(final String id) {
            // when
            final var response = kasseRepo.deleteById(id);

            // then
            assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        }
    }
}
