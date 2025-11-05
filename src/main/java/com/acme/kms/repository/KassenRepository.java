package com.acme.kms.repository;

import com.acme.kms.entity.Kasse;
import java.util.Collection;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import static com.acme.kms.repository.MockDB.getKassen;

@Repository
@SuppressWarnings("PMD")
public class KassenRepository {
    public Collection<Kasse> findAll() {
        return getKassen();
    }

    public Kasse getById(final UUID id) {
        return getKassen().stream()
                .filter(kasse -> kasse.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
