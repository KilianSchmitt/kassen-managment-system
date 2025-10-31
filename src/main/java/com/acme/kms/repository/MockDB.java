// src/main/java/com/acme/kms/repository/MockDB.java
package com.acme.kms.repository;

import com.acme.kms.entity.Bon;
import com.acme.kms.entity.Kasse;
import com.acme.kms.entity.Kassierer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class MockDB {
    private static final List<Kasse> KASSEN = new ArrayList<>();

    static {
        Kassierer kassierer1 = new Kassierer(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "Kilian Schmitt");
        Kassierer kassierer2 = new Kassierer(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"), "Herr Zimmermann");

        Bon bon1 = new Bon(UUID.fromString("123e4567-e89b-12d3-a456-426614174321"), LocalDate.of(2025, 1, 29), new BigDecimal("50.80"));
        Bon bon2 = new Bon(UUID.fromString("123e4567-e89b-12d3-a456-425814174001"), LocalDate.of(2025, 1, 29), new BigDecimal("300"));
        Bon bon3 = new Bon(UUID.fromString("123e4567-e89b-12d3-a456-444614174001"), LocalDate.of(2025, 1, 29), new BigDecimal("0.98"));

        Kasse kasse1 = new KasseBuilder()
            .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174899"))
            .withKassierer(kassierer1)
            .addBon(bon1)
            .addBon(bon2)
            .build();

        Kasse kasse2 = new KasseBuilder()
            .withId(UUID.fromString("123e4567-e89b-12d3-a432-426614174000"))
            .withKassierer(kassierer2)
            .addBon(bon3)
            .build();

        KASSEN.add(kasse1);
        KASSEN.add(kasse2);
    }

    public static List<Kasse> getKassen() {
        return KASSEN;
    }
}