package com.acme.kms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

/// Diese Klasse repräsentiert eine Kasse im System.
/// ![Klassendiagramm](/docs/asciidoc/Kasse.svg)

@Entity
@NamedEntityGraph(name = Kasse.KASSIERER_GRAPH, attributeNodes = @NamedAttributeNode("kassierer"))
@NamedEntityGraph(name = Kasse.KASSIERER_KASSENBONS_GRAPH,
        attributeNodes = {
                @NamedAttributeNode("kassierer"),
                @NamedAttributeNode("kassenBons")
        }
)
public class Kasse {
    public static final String KASSIERER_GRAPH = "Kasse.kassierer";
    public static final String KASSIERER_KASSENBONS_GRAPH = "Kasse.kassierer.kassenBons";

    @Id
    @GeneratedValue
    @Nullable
    private UUID id;

    @NotBlank
    private String bezeichnung;

    @Nullable
    @OneToOne(optional = true, cascade = {PERSIST, REMOVE}, fetch = LAZY, orphanRemoval = true)
    @JoinColumn(name = "kassierer_id")
    private Kassierer kassierer;

    @NotNull
    private BigDecimal bargeldbestand;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "kasse_id", nullable = false)
    @OrderColumn(name = "idx", nullable = false)
    private List<KassenBon> kassenBons;

    @Version
    private int version;

    @CreationTimestamp
    @Nullable
    private LocalDateTime erzeugt;

    @UpdateTimestamp
    @Nullable
    private LocalDateTime aktualisiert;

    // Standard-Konstruktor für _Jakarta Persistence_.
    @SuppressWarnings("NullAway.Init")
    public Kasse(String bezeichnung) {
    }

    @SuppressWarnings("NullAway.Init")
    public Kasse() {
    }

    public Kasse(final UUID id, final String bezeichnung, final Kassierer kassierer, final BigDecimal bargeldbestand, final List<KassenBon> kassenBons, final int version) {
        this.id = id;
        this.bezeichnung = bezeichnung;
        this.kassierer = kassierer;
        this.bargeldbestand = bargeldbestand;
        this.kassenBons = kassenBons;
        this.version = version;
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

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(final String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Kassierer getKassierer() {
        return kassierer;
    }

    public List<KassenBon> getKassenBons() {
        return kassenBons;
    }

    public BigDecimal getBargeldbestand() {
        return bargeldbestand;
    }

    public void setBargeldbestand(final BigDecimal bargeldbestand) {
        this.bargeldbestand = bargeldbestand;
    }

    public void setKassierer(final Kassierer kassierer) {
        this.kassierer = kassierer;
    }

    public void setKassenBons(final List<KassenBon> kassenBons) {
        this.kassenBons = kassenBons;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public @Nullable LocalDateTime getAktualisiert() {
        return aktualisiert;
    }

    public @Nullable LocalDateTime getErzeugt() {
        return erzeugt;
    }

    public void setAktualisiert(@Nullable LocalDateTime aktualisiert) {
        this.aktualisiert = aktualisiert;
    }

    public void set(final Kasse other) {
        this.bezeichnung = other.bezeichnung;
        this.bargeldbestand = other.bargeldbestand;
    }
}
