package com.acme.kms.repository;

import com.acme.kms.entity.Patient;
import java.util.Collection;
import org.springframework.stereotype.Repository;
import static com.acme.kms.repository.MockDB.PATIENTEN;

@Repository
public class PatientenRepository {

    public PatientenRepository() {
    }

    public Collection<Patient> findAll() {
        return PATIENTEN;
    }
}
