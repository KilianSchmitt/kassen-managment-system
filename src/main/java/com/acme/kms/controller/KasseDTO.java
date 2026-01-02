package com.acme.kms.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.UniqueElements;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

/// ValueObject für das Neuanlegen und Ändern einer Kasse. Beim Lesen wird die Klasse KasseModel für die Ausgabe
/// verwendet.
/// @param bezeichnung Die Bezeichnung der Kasse.
/// @param kassierer Der Kassierer der Kasse.
/// @param bargeldbestand Der Bargeldbestand der Kasse.
/// @param kassenBons Die Liste der Kassenbons der Kasse.
public record KasseDTO(
        @NotBlank
        String bezeichnung,

        @Nullable
        @Valid
        KassiererDTO kassierer,

        @PositiveOrZero
        @NotNull
        BigDecimal bargeldbestand,

        @UniqueElements
        @Nullable
        @Valid
        List<KassenBonDTO> kassenBons
) {
    interface OnCreate { }
}
