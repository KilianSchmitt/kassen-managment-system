package com.acme.kms.controller;

import com.acme.kms.entity.Kasse;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record KasseOhneKassenbons(
        @NotNull UUID id,
        @NotNull String bezeichnung,
        @NotNull KassiererDTO kassierer,
        @NotNull BigDecimal betrag
) {
    public static KasseOhneKassenbons of(final Kasse kasse) {
        return new KasseOhneKassenbons(
                kasse.getId(),
                kasse.getBezeichnung(),
                new KassiererDTO(kasse.getKassierer().getVorname(), kasse.getKassierer().getNachname(), kasse.getKassierer().getEmail()),
                kasse.getBargeldbestand()
        );
    }

    public UUID getId() {
        return id();
    }

    public String getBezeichnung() {
        return bezeichnung();
    }

    public KassiererDTO getKassierer() {
        return kassierer();
    }

    public BigDecimal getBetrag() {
        return betrag();
    }
}

