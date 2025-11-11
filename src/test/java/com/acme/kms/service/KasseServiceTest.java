package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KasseRepository;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Tag("unit")
@Tag("service-read")
@ExtendWith(SoftAssertionsExtension.class)
@DisplayName("Geschaeftslogik fuer Lesen testen")
public class KasseServiceTest {

    private static final String ID_VORHANDEN = "00000000-0000-0000-0000-000000000001";
    private static final String KASSIERERNAME_VORHANDEN = "Kilian";

    private final KasseService service;

    @InjectSoftAssertions
    @SuppressWarnings("NullAway.Init")
    private SoftAssertions softly;

    public KasseServiceTest() {
        final var constructor = KasseRepository.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        final KasseRepository repo;
        try {
            repo = (KasseRepository) constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        service = new KasseService(repo);
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID: id={0}")
    @ValueSource(strings = ID_VORHANDEN)
    @DisplayName("Suche Kasse mit id")
    public void findById(final String id) {
        // given
        final var kasseId = UUID.fromString(id);

        // when
        final var kasse = service.findById(kasseId);

        // then
        assertThat(kasse).isNotNull()
            .extracting(Kasse::getId)
            .isEqualTo(kasseId);
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandenem Kassierername: kassiererName={0}")
    @ValueSource(strings = KASSIERERNAME_VORHANDEN)
    @DisplayName("Suche Kasse mit vorhandenem Kassierername")
    void find(final String kassiererName) {
        // given
        final var params = Map.of("kassiererName", kassiererName);

        // when
        final var kassen = service.find(params);

        // then
        softly.assertThat(kassen)
            .isNotNull();
        kassen.stream()
            .map(kasse -> kasse.getKassierer().getName())
            .forEach(name -> softly.assertThat(name)
                    .contains(KASSIERERNAME_VORHANDEN));
    }
}