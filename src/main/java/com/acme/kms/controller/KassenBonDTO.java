package com.acme.kms.controller;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

/// ValueObject für den Kassenbon beim Neuanlegen und Ändern einer Kasse.
/// @param date  Das Datum des Kassenbons.
/// @param betrag Der Betrag des Kassenbons.
record KassenBonDTO(
        @NotNull
        @PastOrPresent
        LocalDate date,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal betrag
) { }
