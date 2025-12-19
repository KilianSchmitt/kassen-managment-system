package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KasseBuilder;
import com.acme.kms.repository.KasseRepository;
import jakarta.mail.internet.MimeMessage;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import com.acme.kms.mail.MailService;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.junit.jupiter.api.condition.JRE.JAVA_25;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Tag("unit")
@Tag("service-write")
@DisplayName("Geschaeftslogik fuer Schreiben")
@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
@EnabledForJreRange(min = JAVA_25, max = JAVA_25)
@SuppressWarnings({
    "InnerTypeLast",
    "ClassFanOutComplexity",
    "MethodOnlyUsedFromInnerClass",
    "WriteTag",
    "PMD.AtLeastOneConstructor"
})
class KasseWriteServiceTest {
    private static final String BEZEICHNUNG = "Kasse 999";
    private static final String BEZEICHNUNG_VORHANDEN = "Kasse 1";
    private static final String ID_VORHANDEN = "40000000-0000-0000-0000-000000000001";
    private static final BigDecimal BARGELDBESTAND_STANDARD = BigDecimal.valueOf(1000.00);

    @Mock
    @SuppressWarnings("NullAway.Init")
    private KasseRepository repo;

    @Mock
    @SuppressWarnings("NullAway.Init")
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    private KasseWriteService service;

    @InjectSoftAssertions
    @SuppressWarnings("NullAway.Init")
    private SoftAssertions softly;

    @BeforeEach
    @SuppressWarnings({"NullAway", "PMD.AvoidAccessibilityAlteration"})
    void beforeEach() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // Konstruktor von MailService ist "package private" und nicht public
        final var mailServiceConstrs = MailService.class.getDeclaredConstructors();
        assertThat(mailServiceConstrs).hasSize(1);
        final var mailServiceConstr = mailServiceConstrs[0];
        mailServiceConstr.setAccessible(true);
        final var mailService = (MailService) mailServiceConstr.newInstance(mailSender);

        service = new KasseWriteService(repo, mailService);
    }

    @Nested
    @DisplayName("Geschaeftslogik fuer Erzeugen")
    class Erzeugen {
        @ParameterizedTest(name = "[{index}] Neuanlegen einer neuen Kasse: bezeichnung={0}")
        @ValueSource(strings = BEZEICHNUNG)
        @DisplayName("Neuanlegen einer neuen Kasse")
        void create(final String bezeichnung) {
            // given
            if (bezeichnung == null) {
                throw new IllegalStateException("Testdaten sind null");
            }

            when(repo.existsByBezeichnung(bezeichnung)).thenReturn(false);
            final var kasseMock = createKasseMock(UUID.fromString(ID_VORHANDEN), bezeichnung);
            when(repo.save(kasseMock)).thenReturn(kasseMock);

            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            doNothing().when(mailSender).send(mimeMessage);

            // when
            final var kasse = service.create(kasseMock);

            // then
            assertThat(kasse).isNotNull();
            softly.assertThat(kasse.getId()).isNotNull();
            softly.assertThat(kasse.getBezeichnung()).isEqualTo(bezeichnung);
        }

        @ParameterizedTest(name = "[{index}] Neuanlegen mit existierender Bezeichnung: {0}")
        @ValueSource(strings = BEZEICHNUNG_VORHANDEN)
        @DisplayName("Neuanlegen mit existierender Bezeichnung")
        void createEmailExists(final String bezeichnung) {
            // given
            if (bezeichnung == null) {
                throw new IllegalStateException("Testdaten sind null");
            }

            when(repo.existsByBezeichnung(bezeichnung)).thenReturn(true);
            final var kasseMock = createKasseMock(bezeichnung);

            // when
            final var emailExistsException = catchThrowableOfType(
                    KasseExistsException.class,
                    () -> service.create(kasseMock)
            );

            // then
            assertThat(emailExistsException).isNotNull();
        }
    }

    @Nested
    @DisplayName("Geschaeftslogik fuer Aendern")
    class Aendern {
        private static final String ID_UPDATE = "40000000-0000-0000-0000-000000000002";
        private static final String ID_NICHT_VORHANDEN = "40000000-0000-0000-0000-000000000099";

        @ParameterizedTest(name = "[{index}] Aendern einer Kasse: id={0}, bezeichnung={1}")
        @CsvSource(ID_UPDATE + ',' + BEZEICHNUNG_VORHANDEN)
        @DisplayName("Aendern einer Kasse")
        void update(final String idStr, final String bezeichnung) {
            // given
            final var id = UUID.fromString(idStr);
            final var kasseMock = createKasseMock(id, bezeichnung);
            when(repo.findById(id)).thenReturn(Optional.of(kasseMock));
            when(repo.save(kasseMock)).thenReturn(kasseMock);

            // when
            final var kasse = service.update(kasseMock, id, kasseMock.getVersion());

            // then
            assertThat(kasse)
                    .isNotNull()
                    .extracting(Kasse::getId)
                    .isEqualTo(kasseMock.getId());
        }

        @ParameterizedTest(name = "[{index}] Aendern einer nicht-vorhandenen Kasse: id={0}, bezeichnung={1}")
        @CsvSource(ID_NICHT_VORHANDEN + ',' + BEZEICHNUNG_VORHANDEN)
        @DisplayName("Aendern einer nicht-vorhandenen Kasse")
        void updateNichtVorhanden(final String idStr, final String bezeichnung) {
            // given
            final var id = UUID.fromString(idStr);
            final var kasseMock = createKasseMock(id, bezeichnung);
            when(repo.findById(id)).thenReturn(Optional.empty());

            // when
            final var notFoundException = catchThrowableOfType(
                    NotFoundException.class,
                    () -> service.update(kasseMock, id, kasseMock.getVersion())
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
            final BigDecimal bargeldbestand
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
