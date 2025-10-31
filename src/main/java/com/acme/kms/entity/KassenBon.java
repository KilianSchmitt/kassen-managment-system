package com.acme.kms.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Bon {
    private UUID id;
    private LocalDate date;
    private BigDecimal betrag;

    public Bon(UUID id, LocalDate date, BigDecimal betrag) {
        this.id = id;
        this.date = date;
        this.betrag = betrag;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Bon bon = (Bon) o;
        return Objects.equals(id, bon.id) && Objects.equals(date, bon.date) && Objects.equals(betrag, bon.betrag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, betrag);
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

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getBetrag() {
        return betrag;
    }

    public void setBetrag(BigDecimal betrag) {
        this.betrag = betrag;
    }
}
