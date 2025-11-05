package com.acme.kms.repository;

import com.acme.kms.entity.Kasse;
import java.util.Collection;
import org.springframework.stereotype.Repository;
import static com.acme.kms.repository.MockDB.getKassen;

@Repository
@SuppressWarnings("PMD")
public class KassenRepository {
    public Collection<Kasse> findAll() {
        return getKassen();
    }
}
