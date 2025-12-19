package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.mail.MailService;
import com.acme.kms.repository.KasseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class KasseWriteService {
    private final KasseRepository repo;
    private final MailService mailService;
    private final StableValue<Logger> logger = StableValue.of();

    /// Konstruktor mit `package private` für _Constructor Injection_ bei _Spring_.
    ///
    /// @param repo Injiziertes Repository für _Spring Data_.
    /// @param mailService Injiziertes Objekt für Mailing.
    public KasseWriteService(final KasseRepository repo, final MailService mailService) {
        this.repo = repo;
        this.mailService = mailService;
    }

    @Transactional
    @SuppressWarnings("TrailingComment")
    public Kasse create(final Kasse kasse) {
        getLogger().debug("create: kasse={}", kasse);

        if (repo.existsByBezeichnung(kasse.getBezeichnung())) {
            throw new KasseExistsException(kasse.getBezeichnung());
        }

        final var kasseDB = repo.save(kasse);

        getLogger().trace("create: Thread-ID={}", Thread.currentThread().threadId());
        mailService.send(kasseDB);

        getLogger().debug("create: kasseDB={}", kasseDB);
        return kasseDB;
    }

    /// Eine vorhandene Kasse aktualisieren.
    ///
    /// @param kasse Das Objekt mit den neuen Daten (ohne ID)
    /// @param id ID der zu aktualisierenden Kasse
    /// @param version Die erforderliche Version
    /// @return Aktualisierte Kasse mit erhöhter Versionsnummer
    /// @throws NotFoundException Keine Kasse zur ID vorhanden.
    /// @throws VersionOutdatedException Die Versionsnummer ist veraltet und nicht aktuell.
    /// @throws KasseExistsException Es gibt bereits eine Kasse mit der Bezeichnung.
    @Transactional
    public Kasse update(final Kasse kasse, final UUID id, final int version) {
        getLogger().debug("update: kasse={}, id={}, version={}", kasse, id, version);

        var kasseDb = repo
                .findById(id)
                .orElseThrow(NotFoundException::new);
        getLogger().trace("update: version={}, kasseDb={}", version, kasseDb);

        if (version != kasseDb.getVersion()) {
            throw new VersionOutdatedException(version);
        }

        final var bezeichnung = kasse.getBezeichnung();
        // Ist die neue Bezeichnung bei einer *anderen* Kasse vorhanden?
        if (!Objects.equals(bezeichnung, kasseDb.getBezeichnung()) && repo.existsByBezeichnung(bezeichnung)) {
            getLogger().debug("update: bezeichnung {} existiert", bezeichnung);
            throw new KasseExistsException(bezeichnung);
        }
        getLogger().trace("update: Kein Konflikt mit der Bezeichnung");

        // Zu überschreibende Werte übernehmen
        kasseDb.set(kasse);
        kasseDb = repo.save(kasseDb);

        getLogger().debug("update: {}", kasseDb);
        return kasseDb;
    }

    /// Eine Kasse löschen.
    ///
    /// @param id Die ID der zu löschenden Kasse.
    @Transactional
    public void deleteById(final UUID id) {
        getLogger().debug("deleteById: id={}", id);
        repo.findById(id).ifPresent(repo::delete);
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseWriteService.class));
    }
}
