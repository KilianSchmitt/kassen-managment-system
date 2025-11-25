package com.acme.kms.repository;

import com.acme.kms.entity.Kasse;
import com.acme.kms.service.KasseExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(KasseRepository.class);

    public Collection<Kasse> findAll() {
        LOGGER.debug("findAll()");
        return getKassen();
    }

    public Collection<Kasse> find(final Map<String, String> queryparam) {
        LOGGER.debug("find: queryparam={}", queryparam);
        if (queryparam.isEmpty()) {
            return findAll();
        }

        if (queryparam.size() == 1) {
            final var idStr = queryparam.get("id");
            if (idStr != null && !idStr.isBlank()) {
                final var kasse = getById(UUID.fromString(idStr.trim()));
                LOGGER.debug("find: kasse={}", kasse);
                return kasse == null ? Collections.emptyList() : List.of(kasse);
            }

            final var kassiererName = queryparam.get("kassiererName");
            if (kassiererName != null && !kassiererName.isBlank()) {
                LOGGER.debug("find: kassiererName={}", kassiererName);
                return findByKassiererName(kassiererName);
            }
        }

        LOGGER.debug("find: wrong queryparam={}", queryparam);
        return Collections.emptyList();
    }

    public Kasse getById(final UUID id) {
        return getKassen().stream()
                .filter(kasse -> kasse.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Kasse> findByKassiererName(final String kassiererName) {
        LOGGER.debug("findByKassiererName: kassiererName={}", kassiererName);
        final var result = getKassen().stream()
                .filter(kasse -> kasse.getKassierer().getVorname().contains(kassiererName) ||
                        kasse.getKassierer().getNachname().contains(kassiererName))
                .toList();
        LOGGER.debug("findByKassiererName: result={}", result);
        return result;
    }

    public boolean isKasseExisting(final UUID id, final String bezeichnung) {
        LOGGER.debug("isKasseExisting: uuid={}, bezeichnung={}", id, bezeichnung);
        if (id != null) {
            return getById(id) != null;
        }
        final var result = getKassen().stream()
                .anyMatch(kasse -> kasse.getBezeichnung().equalsIgnoreCase(bezeichnung));
        LOGGER.debug("isKasseExisting: result={}", result);
        return result;
    }

    public Kasse create(final Kasse kasse) {
        LOGGER.debug("create: {}", kasse);
        kasse.setId(UUID.randomUUID());
        KASSEN.add(kasse);
        LOGGER.debug("create: kasse={}", kasse);
        return kasse;
    }

    public void update(final Kasse kasse, final UUID id) {
        LOGGER.debug("update: kasse={}, id={}", kasse, id);
        final var indexOfKasse = KASSEN.indexOf(getById(id));
        final String orignalBezeichnung = KASSEN.get(indexOfKasse).getBezeichnung();

        if (!orignalBezeichnung.equalsIgnoreCase(kasse.getBezeichnung()) &&
                isKasseExisting(null, kasse.getBezeichnung())) {
            throw new KasseExistsException(kasse.getBezeichnung());
        }

        final Kasse newKasse = new KasseBuilder()
                .withId(id)
                .withBezeichnung(kasse.getBezeichnung())
                .withBargeldbestand(kasse.getBargeldbestand())
                .withKassierer(kasse.getKassierer())
                .withBons(kasse.getKassenBons())
                .build();

        LOGGER.debug("update: newKasse={}", newKasse);
        KASSEN.set(indexOfKasse, newKasse);
    }

    public void delete(final UUID id) {
        LOGGER.debug("delete: id={}", id);
        KASSEN.removeIf(kasse -> kasse.getId().equals(id));
    }

}
