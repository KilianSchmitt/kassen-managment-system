package com.acme.kms.repository;

import com.acme.kms.entity.Kasse;
import com.acme.kms.entity.KassenBon;
import com.acme.kms.entity.Kassierer;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("all")
public class MockDB {
    private static final List<Kasse> KASSEN = new ArrayList<>();

    static {
        final Kassierer kassierer1 = new Kassierer(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "Kilian Schmitt");
        final Kassierer kassierer2 = new Kassierer(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"), "Herr Zimmermann");
        final Kassierer kassierer3 = new Kassierer(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"), "Frau Müller");

        final KassenBon kassenBon1 = new KassenBon(UUID.fromString("123e4567-e89b-12d3-a456-426614174321"), LocalDate.of(2025, 1, 29), new BigDecimal("50.80"));
        final KassenBon kassenBon2 = new KassenBon(UUID.fromString("123e4567-e89b-12d3-a456-425814174001"), LocalDate.of(2025, 1, 29), new BigDecimal("300"));
        final KassenBon kassenBon3 = new KassenBon(UUID.fromString("123e4567-e89b-12d3-a456-444614174001"), LocalDate.of(2025, 1, 29), new BigDecimal("0.98"));
        final KassenBon kassenBon4 = new KassenBon(UUID.fromString("123e4567-e89b-12d3-a456-426614174322"), LocalDate.of(2025, 2, 1), new BigDecimal("120.50"));
        final KassenBon kassenBon5 = new KassenBon(UUID.fromString("123e4567-e89b-12d3-a456-426614174323"), LocalDate.of(2025, 2, 2), new BigDecimal("15.75"));

        final Kasse kasse1 = new KasseBuilder()
            .withId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
            .withKassierer(kassierer1)
            .addBon(kassenBon1)
            .addBon(kassenBon2)
            .build();

        final Kasse kasse2 = new KasseBuilder()
            .withId(UUID.fromString("00000000-0000-0000-0000-000000000002"))
            .withKassierer(kassierer2)
            .addBon(kassenBon3)
            .build();

        final Kasse kasse3 = new KasseBuilder()
                .withId(UUID.fromString("00000000-0000-0000-0000-000000000003"))
                .withKassierer(kassierer3)
                .addBon(kassenBon4)
                .addBon(kassenBon5)
                .build();

        KASSEN.add(kasse3);
        KASSEN.add(kasse1);
        KASSEN.add(kasse2);
    }

    @NotNull
    public static List<Kasse> getKassen() {
        return Collections.unmodifiableList(KASSEN);
    }
}
