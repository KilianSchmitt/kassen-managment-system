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
    private static final Logger LOGGER = LoggerFactory.getLogger(KasseService.class);
    private final KasseRepository repo;

    public KasseService(final KasseRepository repo) {
        this.repo = repo;
    }

    public Collection<Kasse> find(final Map<String, String> queryparam) {
        LOGGER.debug("find: Queryparameter={}", queryparam);
        final var kassen = repo.find(queryparam);
        if (kassen.isEmpty()) {
            throw new NotFoundException();
        }
        LOGGER.debug("find: Kasse={}", kassen);
        return kassen;
    }

    public Kasse findById(final UUID id) {
        LOGGER.debug("findById: id={}", id);
        final var kasse = repo.getById(id);
        if (kasse == null) {
            throw new NotFoundException();
        }
        LOGGER.debug("findById: kasse={}", kasse);
        return kasse;
    }
}
