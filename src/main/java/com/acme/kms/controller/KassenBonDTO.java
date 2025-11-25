package com.acme.kms.controller;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

record KassenBonDTO(
        @NotNull
        @PastOrPresent
        LocalDate date,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal betrag
) { }
