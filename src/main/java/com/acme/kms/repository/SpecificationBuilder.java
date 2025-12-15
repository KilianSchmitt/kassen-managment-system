/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.acme.kms.repository;

import com.acme.kms.entity.Kasse;
import com.acme.kms.entity.Kasse_;
import com.acme.kms.entity.Kassierer_;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.acme.kms.entity.KassenBon_.betrag;

/// Singleton-Klasse, um Specifications für Queries in Spring Data JPA zu bauen.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@Component
public class SpecificationBuilder {
    private final StableValue<Logger> logger = StableValue.of();

    /// Konstruktor mit `package private` für _Spring_.
    SpecificationBuilder() {
    }

    @Nullable
    public Specification<Kasse> build(final Map<String, ? extends List<String>> suchparameter) {
        getLogger().debug("build: suchparameter={}", suchparameter);

        if (suchparameter.isEmpty()) {
            return null;
        }

        final var specs = suchparameter
                .entrySet()
                .stream()
                .map(this::toPredicateSpecification)
                .toList();

        if (specs.isEmpty() || specs.contains(null)) {
            return null;
        }

        return Specification.where(PredicateSpecification.allOf(specs));
    }

    @Nullable
    private PredicateSpecification<Kasse> toPredicateSpecification(
            final Map.Entry<String, ? extends List<String>> entry
    ) {
        getLogger().trace("toSpec: entry={}", entry);
        final var key = entry.getKey();
        final var values = entry.getValue();

        if (values.size() != 1) {
            return null;
        }

        final var value = values.getFirst();
        return switch (key) {
            case "bezeichnung" -> bezeichnung(value);
            case "bargeldbestand" -> bargeldbestand(value);
            case "kassiererVorname" -> kassiererVorname(value);
            case "kassiererNachname" -> kassiererNachname(value);
            default -> null;
        };
    }

    private PredicateSpecification<Kasse> bezeichnung(final String teil) {
        return (root, builder) -> builder.like(
            builder.lower(root.get("bezeichnung")),
            builder.lower(builder.literal("%" + teil + "%"))
        );
    }

    @Nullable
    private PredicateSpecification<Kasse> bargeldbestand(final String betrag) {
        final BigDecimal betragDecimal;
        try {
            betragDecimal = new BigDecimal(betrag);
        } catch (NumberFormatException _) {
            return null;
        }
        return (root, builder) -> builder.equal(root.get(Kasse_.bargeldbestand), betragDecimal);
    }

    private PredicateSpecification<Kasse> kassiererVorname(final String teil) {
        return (root, builder) -> builder.like(
                builder.lower(root.get(Kasse_.kassierer).get(Kassierer_.vorname)),
                builder.lower(builder.literal("%" + teil + "%"))
        );
    }

    private PredicateSpecification<Kasse> kassiererNachname(final String teil) {
        return (root, builder) -> builder.like(
                builder.lower(root.get(Kasse_.kassierer).get(Kassierer_.nachname)),
                builder.lower(builder.literal("%" + teil + "%"))
        );
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(SpecificationBuilder.class));
    }
}
