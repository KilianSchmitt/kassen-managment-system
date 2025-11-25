package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KasseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KasseWriteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KasseWriteService.class);
    private final KasseRepository repo;

    public KasseWriteService(final KasseRepository repo) {
        this.repo = repo;
    }

    public Kasse create(final Kasse kasse) {
        LOGGER.debug("Creating kasse: {}", kasse);
        if (repo.isKasseExisting(null, kasse.getBezeichnung())) {
            throw new KasseExistsException(kasse.getBezeichnung());
        }
        return repo.create(kasse);
    }

    public void update(final Kasse kasse, final UUID id) {
        LOGGER.debug("Updating kasse with id {}", id);
        repo.update(kasse, id);
    }

    public void delete(final UUID id) {
        LOGGER.debug("Deleting kasse with id {}", id);
        if (repo.getById(id) == null) {
            throw new NotFoundException();
        }
        repo.delete(id);
    }
}
