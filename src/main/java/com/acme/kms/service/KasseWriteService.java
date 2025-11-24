package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KasseRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KasseWriteService {
    private final KasseRepository repo;

    public KasseWriteService(final KasseRepository repo) {
        this.repo = repo;
    }

    public Kasse create(final Kasse kasse) {
        if (repo.isKasseExisting(null, kasse.getBezeichnung()))
            throw new KasseExistsException(kasse.getBezeichnung());

        return repo.create(kasse);
    }

    public void update(final Kasse kasse, final UUID id) {
        if (repo.isKasseExisting(null, kasse.getBezeichnung()))
            throw new KasseExistsException(kasse.getBezeichnung());

        repo.update(kasse, id);
    }

    public void delete(final UUID id) {
        if (repo.getById(id) == null)
            throw new NotFoundException();

        repo.delete(id);
    }
}
