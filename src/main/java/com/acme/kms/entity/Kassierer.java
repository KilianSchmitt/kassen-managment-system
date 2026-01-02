package com.acme.kms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

@Entity
public class Kassierer {
    @Id
    @GeneratedValue
    @NotNull
    private UUID id;

    @NotBlank
    private String vorname;

    @NotBlank
    private String nachname;

    @NotBlank
    private String email;

    @SuppressWarnings("NullAway.Init")
    public Kassierer() {
    }

    public Kassierer(final UUID id, final String vorname, final String nachname, final String email) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Kassierer{" +
                "id=" + id +
                ", vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(final String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(final String nachname) {
        this.nachname = nachname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Kassierer kassierer && Objects.equals(id, kassierer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
