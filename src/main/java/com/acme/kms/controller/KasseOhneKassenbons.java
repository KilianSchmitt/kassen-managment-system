package com.acme.kms.controller;

import com.acme.kms.entity.Kasse;
import com.acme.kms.entity.Kassierer;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.UUID;

/// ValueObject für die Darstellung einer Kasse ohne die zugehörigen Kassenbons.
/// @param id Die ID der Kasse.
/// @param bezeichnung Die Bezeichnung der Kasse.
/// @param kassierer Der Kassierer der Kasse.
/// @param bargeldbestand Der Bargeldbestand der Kasse.
public record KasseOhneKassenbons(
        @Nullable UUID id,
        String bezeichnung,
        @Nullable Kassierer kassierer,
        BigDecimal bargeldbestand
) {
    static KasseOhneKassenbons of(final Kasse kasse) {
        return new KasseOhneKassenbons(
                kasse.getId(),
                kasse.getBezeichnung(),
                kasse.getKassierer(),
                kasse.getBargeldbestand()
        );
    }
}


