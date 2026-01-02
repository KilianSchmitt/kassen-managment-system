package com.acme.kms.repository;

import com.acme.kms.entity.Kasse;
import com.acme.kms.entity.KassenBon;
import com.acme.kms.entity.Kassierer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("PMD")
public class KasseBuilder {
    private UUID id;
    private String bezeichnung;
    private Kassierer kassierer;
    private BigDecimal bargeldbestand;
    private List<KassenBon> kassenBons = new ArrayList<>();
    private int version;

    public KasseBuilder withId(final UUID newId) {
        this.id = newId;
        return this;
    }

    public KasseBuilder withBezeichnung(final String newBezeichnung) {
        this.bezeichnung = newBezeichnung;
        return this;
    }

    public KasseBuilder withKassierer(final Kassierer newKassierer) {
        this.kassierer = newKassierer;
        return this;
    }

    public KasseBuilder withBargeldbestand(final BigDecimal newBargeldbestand) {
        this.bargeldbestand = newBargeldbestand;
        return this;
    }

    public KasseBuilder withBons(final List<KassenBon> newKassenBons) {
        this.kassenBons = new ArrayList<>(newKassenBons);
        return this;
    }

    public KasseBuilder withVersion(final int newVersion) {
        this.version = newVersion;
        return this;
    }

    public Kasse build() {
        return new Kasse(id, bezeichnung, kassierer, bargeldbestand, kassenBons, version);
    }
}
