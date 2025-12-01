package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KasseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/// Eine Service-Klasse kapselt die Geschäftslogik und wird von der Controller-Klasse genutzt.
/// Methoden dieser Klasse werden von der REST-Schnittstelle aufgerufen.
/// ![Klassendiagramm](/docs/asciidoc/KasseWriteService.svg)
@Service
public class KasseWriteService {
    private final StableValue<Logger> logger = StableValue.of();
    private final KasseRepository repo;

    /// Konstruktor mit _package private_ für _Spring_.
    /// @param repo Injiziertes Repository-Objekt.
    KasseWriteService(final KasseRepository repo) {
        this.repo = repo;
    }

    /// Eine neue Kasse anlegen.
    /// @param kasse Das Objekt der neu anzulegenden Kasse.
    /// @return Die neu angelegte Kasse mit generierter ID
    /// @throws KasseExistsException Es gibt bereits eine Kasse mit der Bezeichnung.
    public Kasse create(final Kasse kasse) {
        getLogger().debug("Creating kasse: {}", kasse);
        if (repo.isKasseExisting(null, kasse.getBezeichnung())) {
            throw new KasseExistsException(kasse.getBezeichnung());
        }
        return repo.create(kasse);
    }

    /// Eine vorhandene Kasse aktualisieren.
    /// @param kasse Das Objekt mit den neuen Daten (ohne ID)
    /// @param id ID der zu aktualisierenden Kasse
    /// @throws NotFoundException Keine Kasse zur ID vorhanden.
    /// @throws KasseExistsException Es gibt bereits eine Kasse mit der Bezeichnung.
    public void update(final Kasse kasse, final UUID id) {
        getLogger().debug("Updating kasse with id {}", id);
        repo.update(kasse, id);
    }

    /// Eine vorhandene Kasse löschen.
    /// @param id Die ID der zu löschenden Kasse.
    /// @throws NotFoundException Keine Kasse zur ID vorhanden.
    public void delete(final UUID id) {
        getLogger().debug("Deleting kasse with id {}", id);
        if (repo.getById(id) == null) {
            throw new NotFoundException();
        }
        repo.delete(id);
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseRepository.class));
    }
}
