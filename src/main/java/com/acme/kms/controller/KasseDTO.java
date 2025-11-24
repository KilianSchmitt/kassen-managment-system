package com.acme.kms.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.UniqueElements;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

public record KasseDTO(
        @NotBlank
        String bezeichnung,

        @NotNull
        @Valid
        KassiererDTO kassierer,

        @PositiveOrZero
        BigDecimal bargeldbestand,

        @UniqueElements
        @Nullable
        @Valid
        List<KassenBonDTO> kassenBons
) {
    interface OnCreate { }
}
