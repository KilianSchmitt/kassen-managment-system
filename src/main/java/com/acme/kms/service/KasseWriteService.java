package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KasseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class KasseWriteService {
    private final KasseRepository repo;
    private final StableValue<Logger> logger = StableValue.of();

    /// Konstruktor mit `package private` für _Constructor Injection_ bei _Spring_.
    ///
    /// @param repo Injiziertes Repository für _Spring Data_.
    public KasseWriteService(KasseRepository repo) {
        this.repo = repo;
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
        // Optional: mailService.send(kasseDB);

        getLogger().debug("create: kasseDB={}", kasseDB);
        return kasseDB;
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseWriteService.class));
    }
}
