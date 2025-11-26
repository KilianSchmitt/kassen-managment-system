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

    /// Kassen anhand von Suchparametern ermitteln.
    /// @param queryparam Query-Parameter als Map.
    /// @return Gefundene Kassen als [Collection].
    /// @throws NotFoundException Keine Kasse gefunden.
    public Collection<Kasse> find(final Map<String, String> queryparam) {
        getLogger().debug("find: Queryparameter={}", queryparam);
        final var kassen = repo.find(queryparam);
        if (kassen.isEmpty()) {
            throw new NotFoundException();
        }
        getLogger().debug("find: Kasse={}", kassen);
        return kassen;
    }

    /// Eine Kasse anhand ihrer ID suchen.
    /// @param id ID der zu suchenden Kasse
    /// @return Gefundene Kasse.
    /// @throws NotFoundException Kasse nicht gefunden.
    public Kasse findById(final UUID id) {
        getLogger().debug("findById: id={}", id);
        final var kasse = repo.getById(id);
        if (kasse == null) {
            throw new NotFoundException();
        }
        getLogger().debug("findById: kasse={}", kasse);
        return kasse;
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseRepository.class));
    }
}
