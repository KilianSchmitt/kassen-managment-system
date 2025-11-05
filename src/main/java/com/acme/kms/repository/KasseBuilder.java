package com.acme.kms.repository;

import com.acme.kms.entity.Kasse;
import com.acme.kms.entity.KassenBon;
import com.acme.kms.entity.Kassierer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("PMD")
public class KasseBuilder {
    private UUID id;
    private Kassierer kassierer;
    private List<KassenBon> kassenBons = new ArrayList<>();

    public KasseBuilder withId(final UUID newId) {
        this.id = newId;
        return this;
    }

    public KasseBuilder withKassierer(final Kassierer newKassierer) {
        this.kassierer = newKassierer;
        return this;
    }

    public KasseBuilder withBons(final List<KassenBon> newKassenBons) {
        this.kassenBons = new ArrayList<>(newKassenBons);
        return this;
    }


    public KasseBuilder addBon(final KassenBon kassenBon) {
        this.kassenBons.add(kassenBon);
        return this;
    }

    public Kasse build() {
        return new Kasse(id, kassierer, kassenBons);
    }
}
