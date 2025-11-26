package com.acme.kms.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/// ValueObject für den Kassierer beim Neuanlegen und Ändern einer Kasse.
/// @param vorname  Gültiger Vorname des Kassierers, d.h. mit einem geeigneten Muster.
/// @param nachname Gültiger Nachname des Kassierers, d.h. mit einem geeigneten Muster.
/// @param email    Email des Kassierers.
public record KassiererDTO(
        @NotBlank
        @Pattern(regexp = VORNAME_PATTERN)
        String vorname,

        @NotBlank
        @Pattern(regexp = NACHNAME_PATTERN)
        String nachname,

        @NotBlank
        @Email
        String email
) {
    public static final String NACHNAME_PATTERN = "(o'|von|von der|von und zu|van)?[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
    public static final String VORNAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
}
