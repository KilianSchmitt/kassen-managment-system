// src/main/java/com/acme/kms/service/KasseService.java
package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KasseRepository;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KasseService {
    private final StableValue<Logger> logger = StableValue.of();
    private final KasseRepository repo;

    /// Konstruktor mit _package private_ für _Spring_.
    /// @param repo Injiziertes Repository-Objekt.
    KasseService(final KasseRepository repo) {
        this.repo = repo;
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

        final var kasse = repo.findByIdFetchKassiererAndKassenbons(id);
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
