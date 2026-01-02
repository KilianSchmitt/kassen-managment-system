// src/main/java/com/acme/kms/service/KasseService.java
package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KasseRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.acme.kms.repository.SpecificationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class KasseService {
    private final KasseRepository repo;
    private final SpecificationBuilder specificationBuilder;
    private final StableValue<Logger> logger = StableValue.of();

    /// Konstruktor mit _package private_ für _Spring_.
    /// @param repo Injiziertes Repository-Objekt.
    KasseService(final KasseRepository repo, final SpecificationBuilder specificationBuilder) {
        this.repo = repo;
        this.specificationBuilder = specificationBuilder;
    }

    /// Kunden anhand von Suchparameter als Collection suchen.
    ///
    /// @param suchparameter Die Suchparameter als Map.
    /// @param pageable Seitennummerierung mit Spring Data.
    /// @return Die gefundenen Kunden oder eine leere Liste.
    /// @throws NotFoundException Falls keine Kunden gefunden wurden.
    @SuppressWarnings({"ReturnCount", "PMD.AvoidLiteralsInIfCondition"})
    public Page<Kasse> find(final Map<String, List<String>> suchparameter, final Pageable pageable) {
        getLogger().debug("find: suchparameter={}, pageable={}", suchparameter, pageable);

        if (suchparameter.isEmpty()) {
            return repo.findAll(pageable);
        }

        // vordefinierte Query für suche mit bezeichnung
        if (suchparameter.size() == 1) {
            final var bezeichnung = suchparameter.get("bezeichnung");
            if (bezeichnung != null && bezeichnung.size() == 1) {
                return findByBezeichnung(bezeichnung.getFirst(), pageable);
            }
        }

        final var specification = specificationBuilder.build(suchparameter);
        if (specification == null) {
            throw new NotFoundException();
        }
        final var kassePage = repo.findAll(specification, pageable);
        if (kassePage.isEmpty()) {
            throw new NotFoundException();
        }
        getLogger().debug("find: {}, {}", kassePage, kassePage.getContent());
        return kassePage;
    }

    private Page<Kasse> findByBezeichnung(
        final String bezeichnung,
        final Pageable pageable
    ) {
        getLogger().trace("findByBezeichnung: {}", bezeichnung);
        final var kassePage = repo.findByBezeichnung(bezeichnung, pageable);
        if (kassePage.isEmpty()) {
            throw new NotFoundException();
        }
        getLogger().trace("findByBezeichnung: {}", kassePage);
        return kassePage;
    }

    public Kasse findByIdMitKassierer(final UUID id) {
        getLogger().debug("findByIdMitKassierer: id={}", id);

        final var kasse = repo.findByIdFetchKassierer(id);
        getLogger().trace("findByIdMitKassierer: kasse={}", kasse);

        if (kasse == null) {
            throw new NotFoundException();
        }
        getLogger().debug("findByIdMitKassierer: kasse={}", kasse);
        return kasse;
    }

    public Kasse findByIdMitKassiererUndKassenbons(final UUID id) {
        getLogger().debug("findByIdMitKassiererAndKassenbons: id={}", id);

        final var kasse = repo.findByIdFetchKassiererUndKassenbons(id);
        getLogger().trace("findByIdMitKassiererAndKassenbons: kasse={}", kasse);

        if (kasse == null) {
            throw new NotFoundException();
        }
        getLogger().debug("findByIdMitKassiererAndKassenbons: kasse={}, kassenbons={}", kasse, kasse.getKassenBons());
        return kasse;
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseRepository.class));
    }
}
