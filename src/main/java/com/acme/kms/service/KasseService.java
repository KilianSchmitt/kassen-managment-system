package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KassenRepository;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class KasseService {
    private final KassenRepository repo;

    public KasseService(final KassenRepository repo) {
        this.repo = repo;
    }

    public Collection<Kasse> findAll() {
        final var kasse = repo.findAll();
        if (kasse == null) {
            throw new NotFoundException();
        }
        return kasse;
    }
}
