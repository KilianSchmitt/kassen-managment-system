package com.acme.kms.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class KassenBon {
    private UUID id;
    private LocalDate date;
    private BigDecimal betrag;

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
}
