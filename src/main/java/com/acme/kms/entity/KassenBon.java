package com.acme.kms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
public class KassenBon {
    @Id
    @GeneratedValue
    @NotNull
    private UUID id;

    @NotNull
    private LocalDate date;

    @NotNull
    private BigDecimal betrag;

    @SuppressWarnings("NullAway.Init")
    public KassenBon() {
    }

    public KassenBon(final UUID id, final LocalDate date, final BigDecimal betrag) {
        this.id = id;
        this.date = date;
        this.betrag = betrag;
    }

    @Override
    public String toString() {
        return "Bon{" +
                "id=" + id +
                ", date=" + date +
                ", betrag=" + betrag +
                '}';
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public BigDecimal getBetrag() {
        return betrag;
    }

    public void setBetrag(final BigDecimal betrag) {
        this.betrag = betrag;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof KassenBon kassenBon && Objects.equals(id, kassenBon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
