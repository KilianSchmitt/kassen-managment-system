package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KasseBuilder;
import com.acme.kms.repository.KasseRepository;
import com.acme.kms.repository.SpecificationBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.MultiValueMap;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.junit.jupiter.api.condition.JRE.JAVA_25;
import static org.mockito.Mockito.when;

@Tag("unit")
@Tag("service-read")
@DisplayName("Geschaeftslogik fuer Lesen")
@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
@EnabledForJreRange(min = JAVA_25, max = JAVA_25)
@SuppressWarnings({
    "ClassFanOutComplexity",
    "InnerTypeLast",
    "WriteTag",
    "TypeMayBeWeakened",
    "PMD"
})
class KasseServiceTest {
    private static final String BEZEICHNUNG = "Kasse";
    private static final String ID_VORHANDEN = "40000000-0000-0000-0000-000000000001";
    private static final String ID_NICHT_VORHANDEN = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    private static final BigDecimal BARGELDBESTAND_STANDARD = BigDecimal.valueOf(1000.00);

    @Mock
    @SuppressWarnings("NullAway.Init")
    private KasseRepository repo;

    // Kein Mocking: Specification<T> ist ein Interface mit *vielen* Methodensignaturen
    private final SpecificationBuilder specificationBuilder;

    private KasseService service;

    @InjectSoftAssertions
    @SuppressWarnings("NullAway.Init")
    private SoftAssertions softly;

    private final PageRequest pageRequest0 = PageRequest.of(0, 5);

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    KasseServiceTest() {
        final var constructor = SpecificationBuilder.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        try {
            specificationBuilder = (SpecificationBuilder) constructor.newInstance();
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @BeforeEach
    void beforeEach() {
        service = new KasseService(repo, specificationBuilder);
    }

    @Test
    @DisplayName("Immer erfolgreich")
    void immerErfolgreich() {
        assertThat(true).isTrue(); // NOSONAR
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandener Bezeichnung: {0}")
    @ValueSource(strings = BEZEICHNUNG)
    @DisplayName("Suche mit vorhandener Bezeichnung")
    void findByBezeichnung(final String bezeichnung) {
        // given
        final var kasse = createKasseMock(bezeichnung);
        final var kasseMock = new PageImpl<>(List.of(kasse));
        when(repo.findByBezeichnung(bezeichnung, pageRequest0)).thenReturn(kasseMock);
        final var suchparameter = MultiValueMap.fromSingleValue(Map.of("bezeichnung", bezeichnung));

        // when
        final var kunden = service.find(suchparameter, pageRequest0);

        // then
        assertThat(kunden)
                .isNotNull()
                .isNotEmpty();
        kunden
                .stream()
                .map(Kasse::getBezeichnung)
                .forEach(nachnameKunde -> softly.assertThat(nachnameKunde).containsIgnoringCase(bezeichnung));
    }

    @ParameterizedTest(name = "[{index}] Suche mit nicht vorhandener Bezeichnung: {0}")
    @ValueSource(strings = BEZEICHNUNG)
    @DisplayName("Suche mit nicht vorhandener Bezeichnung wirkt NotFoundException")
    void findByBezeichnungNotFound(final String bezeichnung) {
        // given
        final PageImpl<Kasse> kasseMock = new PageImpl<>(List.of());
        when(repo.findByBezeichnung(bezeichnung, pageRequest0))
                .thenReturn(kasseMock);
        final var suchparameter = MultiValueMap.fromSingleValue(Map.of("bezeichnung", bezeichnung));

        // when & then
        softly.assertThatThrownBy(() -> service.find(suchparameter, pageRequest0))
                .isInstanceOf(NotFoundException.class);
    }

    @Nested
    @DisplayName("Geschaeftslogik fuer die Suche anhand der ID")
    @SuppressWarnings("DirectInvocationOnMock")
    class FindById {
        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID: id={0}")
        @ValueSource(strings = ID_VORHANDEN)
        @DisplayName("Suche mit vorhandener ID")
        void findById(final String idStr) {
            // given
            final var id = UUID.fromString(idStr);
            final var kasseMock = createKasseMock(id, BEZEICHNUNG);
            when(repo.findByIdFetchKassierer(id)).thenReturn(kasseMock);

            // when
            final var kasse = service.findByIdMitKassierer(id);

            // then
            assertThat(kasse.getId()).isEqualTo(kasseMock.getId());
        }

        @ParameterizedTest(name = "[{index}] Suche mit nicht vorhandener ID: id={0}")
        @ValueSource(strings = ID_NICHT_VORHANDEN)
        @DisplayName("Suche mit nicht vorhandener ID")
        void findByIdNichtVorhanden(final String idStr) {
            // given
            final var id = UUID.fromString(idStr);

            // when
            final var notFoundException = catchThrowableOfType(
                    NotFoundException.class,
                    () -> service.findByIdMitKassierer(id)
            );

            // then
            assertThat(notFoundException).isNotNull();
        }
    }


    // -------------------------------------------------------------------------
    // Hilfsmethoden fuer Mock-Objekte
    // -------------------------------------------------------------------------
    private Kasse createKasseMock(final String bezeichnung) {
        return createKasseMock(randomUUID(), bezeichnung);
    }

    private Kasse createKasseMock(final UUID id, final String bezeichnung) {
        return createKasseMock(id, bezeichnung, BARGELDBESTAND_STANDARD);
    }

    private Kasse createKasseMock(
            final UUID id,
            final String bezeichnung,
            final java.math.BigDecimal bargeldbestand
    ) {
        return new KasseBuilder()
                .withId(id)
                .withBezeichnung(bezeichnung)
                .withKassierer(null)
                .withBargeldbestand(bargeldbestand)
                .withBons(List.of())
                .build();
    }
}
