// src/main/java/com/acme/kms/service/KasseService.java
package com.acme.kms.service;

import com.acme.kms.entity.Kasse;
import com.acme.kms.repository.KassenRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"checkstyle:ReturnCount", "PMD.AvoidLiteralsInIfCondition"})
public class KasseService {
    private final KassenRepository repo;

    public KasseService(final KassenRepository repo) {
        this.repo = repo;
    }

    public Collection<Kasse> find(final Map<String, String> queryparam) {
        if (queryparam.size() != 1) {
            return Collections.emptyList();
        }

        final var idStr = queryparam.get("id");
        if (idStr != null) {
            try {
                final UUID id = UUID.fromString(idStr.trim());
                return findByIdAsCollection(id);
            } catch (IllegalArgumentException e) {
                return Collections.emptyList();
            }
        }

        final var kassiererName = queryparam.get("kassiererName");
        if (kassiererName != null) {
            return findByKassiererName(kassiererName);
        }

        return Collections.emptyList();
    }

    private Collection<Kasse> findByIdAsCollection(final UUID id) {
        final var kasse = repo.getById(id);
        return kasse != null ? List.of(kasse) : Collections.emptyList();
    }

    private Collection<Kasse> findByKassiererName(final String name) {
        return repo.findAll().stream()
                .filter(k -> k.getKassierer() != null && k.getKassierer().getName().contains(name))
                .toList();
    }

    public Kasse findById(final UUID id) {
        final var kasse = repo.getById(id);
        if (kasse == null) {
            throw new NotFoundException();
        }
        return kasse;
    }
}
