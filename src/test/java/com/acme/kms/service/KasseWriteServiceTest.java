package com.acme.kms.service;

import com.acme.kms.repository.KasseBuilder;
import com.acme.kms.repository.KasseRepository;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@Tag("service-write")
@DisplayName("KasseWriteService testen")
@Execution(CONCURRENT)
@ExtendWith(SoftAssertionsExtension.class)
class KasseWriteServiceTest {

    private static final String NEW_BEZ = "Kasse-Unit";
    private static final BigDecimal NEUER_BESTAND = BigDecimal.valueOf(1000.00);
    private static final String ID_UPDATE = "00000000-0000-0000-0000-000000000002";

    private final KasseRepository repo = new KasseRepository();
    private final KasseWriteService service = new KasseWriteService(repo);

    @InjectSoftAssertions
    private SoftAssertions softly;

    @ParameterizedTest(name = "[{index}] Neuanlegen einer neuen Kasse: name={0}")
    @ValueSource(strings = NEW_BEZ)
    @DisplayName("Neuanlegen einer neuen Kasse")
    void create(final String name) {
        // given
        var kasse = new KasseBuilder()
                .withBezeichnung(name)
                .withBargeldbestand(NEUER_BESTAND)
                .withKassierer(null)
                .withBons(List.of())
                .build();

        // when
        var kasseCreated = service.create(kasse);

        // then
        softly.assertThat(kasseCreated.getId()).isNotNull();
        softly.assertThat(kasseCreated.getBezeichnung()).isEqualTo(NEW_BEZ);
        softly.assertThat(kasseCreated.getBargeldbestand()).isEqualTo(NEUER_BESTAND);
    }

    @ParameterizedTest(name = "[{index}] Aendern einer vorhandenen Kasse: id={0}")
    @ValueSource(strings = ID_UPDATE)
    @DisplayName("Aendern einer vorhandenen Kasse")
    void update(final String id) {
        // given
        var kasseId = UUID.fromString(id);
        var kasse = repo.getById(kasseId);
        assertThat(kasse).isNotNull();
        kasse.setBargeldbestand(BigDecimal.valueOf(666.66));

        // when
        service.update(kasse, kasseId);

        // then
        var result = repo.getById(kasseId);
        assertThat(result).isNotNull();
        assertThat(result.getBargeldbestand()).isEqualTo(BigDecimal.valueOf(666.66));
    }

    @ParameterizedTest(name = "[{index}] Loeschen einer vorhandenen Kasse: id={0}")
    @ValueSource(strings = "00000000-0000-0000-0000-000000000003")
    @DisplayName("Loeschen einer vorhandenen Kasse")
    void deleteById(final String id) {
        // given
        var kasseId = UUID.fromString(id);

        // when
        service.delete(kasseId);

        // then
        var result = repo.getById(kasseId);
        assertThat(result).isNull();
    }
}
