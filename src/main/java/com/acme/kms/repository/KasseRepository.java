package com.acme.kms.repository;

import com.acme.kms.entity.Kasse;
import com.acme.kms.service.KasseExistsException;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import static com.acme.kms.repository.MockDB.KASSEN;
import static com.acme.kms.repository.MockDB.getKassen;

@Repository
@SuppressWarnings("PMD")
public class KasseRepository {
    public Collection<Kasse> findAll() {
        return getKassen();
    }

    public Collection<Kasse> find(final Map<String, String> queryparam) {
        if (queryparam.isEmpty()) {
            return findAll();
        }

        if (queryparam.size() == 1) {
            final var idStr = queryparam.get("id");
            if (idStr != null && !idStr.isBlank()) {
                final var kasse = getById(UUID.fromString(idStr.trim()));
                return kasse == null ? Collections.emptyList() : List.of(kasse);
            }

            final var kassiererName = queryparam.get("kassiererName");
            if (kassiererName != null && !kassiererName.isBlank()) {
                return findByKassiererName(kassiererName);
            }
        }

        return Collections.emptyList();
    }

    public Kasse getById(final UUID id) {
        return getKassen().stream()
                .filter(kasse -> kasse.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Kasse> findByKassiererName(final String kassiererName) {
        return getKassen().stream()
                .filter(kasse -> kasse.getKassierer().getVorname().contains(kassiererName)
                        || kasse.getKassierer().getNachname().contains(kassiererName))
                .toList();
    }

    public boolean isKasseExisting(final UUID id, final String bezeichnung) {
        if (id != null)
            return getById(id) != null;

        return getKassen().stream()
                .anyMatch(kasse -> kasse.getBezeichnung().equalsIgnoreCase(bezeichnung));
    }

    public Kasse create(final Kasse kasse) {
        kasse.setId(UUID.randomUUID());
        KASSEN.add(kasse);
        return kasse;
    }

    public void update(final Kasse kasse, final UUID id) {
        final var indexOfKasse = KASSEN.indexOf(getById(id));
        final String orignalBezeichnung = KASSEN.get(indexOfKasse).getBezeichnung();

        if (!orignalBezeichnung.equalsIgnoreCase(kasse.getBezeichnung())
                && isKasseExisting(null, kasse.getBezeichnung())) {
            throw new KasseExistsException(kasse.getBezeichnung());
        }

        final Kasse newKasse = new KasseBuilder()
                .withId(id)
                .withBezeichnung(kasse.getBezeichnung())
                .withBargeldbestand(kasse.getBargeldbestand())
                .withKassierer(kasse.getKassierer())
                .withBons(kasse.getKassenBons())
                .build();

        KASSEN.set(indexOfKasse, newKasse);
    }

    public void delete(final UUID id) {
        KASSEN.removeIf(kasse -> kasse.getId().equals(id));
    }

}
