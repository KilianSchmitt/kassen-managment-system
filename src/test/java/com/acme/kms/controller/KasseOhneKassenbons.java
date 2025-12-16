package com.acme.kms.controller;

import com.acme.kms.entity.Kasse;
import java.util.UUID;

public record KasseOhneKassenbons(UUID id, String bezeichnung) {
    public static KasseOhneKassenbons of(final Kasse kasse) {
        return new KasseOhneKassenbons(kasse.getId(), kasse.getBezeichnung());
    }
}
