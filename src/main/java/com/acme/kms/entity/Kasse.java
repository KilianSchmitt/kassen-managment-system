package com.acme.kms.entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Kasse {
    private UUID id;
    private Kassierer kassierer;
    private List<KassenBon> kassenBons;

    public Kasse(final UUID id, final Kassierer kassierer, final List<KassenBon> kassenBons) {
        this.id = id;
        this.kassierer = kassierer;
        this.kassenBons = kassenBons;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Kasse kasse && Objects.equals(id, kasse.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UUID getId() {
        return id;
    }

    public Kassierer getKassierer() {
        return kassierer;
    }

    public List<KassenBon> getKassenBons() {
        return kassenBons;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public void setKassierer(final Kassierer kassierer) {
        this.kassierer = kassierer;
    }

    public void setKassenBons(final List<KassenBon> kassenBons) {
        this.kassenBons = kassenBons;
    }
}
