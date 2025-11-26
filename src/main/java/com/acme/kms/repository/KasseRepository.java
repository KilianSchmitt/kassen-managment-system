package com.acme.kms.repository;

import com.acme.kms.entity.Kasse;
import com.acme.kms.service.KasseExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
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
    private final StableValue<Logger> logger = StableValue.of();

    /// Alle Kassen als Collection ermitteln.
    /// @return Collection mit allen Kassen
    public Collection<Kasse> findAll() {
        getLogger().debug("findAll()");
        return getKassen();
    }

    /// Kasse anhand von Suchparametern ermitteln
    /// @param queryparam Suchparameter
    /// @return Gefundene Kassen oder leere Collection
    public Collection<Kasse> find(final Map<String, String> queryparam) {
        getLogger().debug("find: queryparam={}", queryparam);
        if (queryparam.isEmpty()) {
            return findAll();
        }

        if (queryparam.size() == 1) {
            final var idStr = queryparam.get("id");
            if (idStr != null && !idStr.isBlank()) {
                final var kasse = getById(UUID.fromString(idStr.trim()));
                getLogger().debug("find: kasse={}", kasse);
                return kasse == null ? Collections.emptyList() : List.of(kasse);
            }

            final var kassiererName = queryparam.get("kassiererName");
            if (kassiererName != null && !kassiererName.isBlank()) {
                getLogger().debug("find: kassiererName={}", kassiererName);
                return findByKassiererName(kassiererName);
            }
        }

        getLogger().debug("find: wrong queryparam={}", queryparam);
        return Collections.emptyList();
    }

    /// Einen Kasse anhand seiner ID suchen.
    /// @param id Die Id der gesuchten Kasse
    /// @return Gefundene Kasse oder null
    @Nullable
    public Kasse getById(final UUID id) {
        return getKassen().stream()
                .filter(kasse -> kasse.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /// Kassen anhand des Kassierernamens ermitteln.
    /// @param kassiererName Der Name des Kassierers
    /// @return Gefundene Kassen oder leere Collection
    @Nullable
    public List<Kasse> findByKassiererName(final String kassiererName) {
        getLogger().debug("findByKassiererName: kassiererName={}", kassiererName);
        final var result = getKassen().stream()
                .filter(kasse -> kasse.getKassierer().getVorname().contains(kassiererName) ||
                        kasse.getKassierer().getNachname().contains(kassiererName))
                .toList();
        getLogger().debug("findByKassiererName: result={}", result);
        return result;
    }

    /// Abfrage, ob eine Kasse mit der gegebenen ID oder Bezeichnung existiert.
    /// @param id ID der Kasse
    /// @param bezeichnung Bezeichnung der Kasse
    /// @return true, falls die Kasse existiert, sonst false
    public boolean isKasseExisting(final UUID id, final String bezeichnung) {
        getLogger().debug("isKasseExisting: uuid={}, bezeichnung={}", id, bezeichnung);
        if (id != null) {
            return getById(id) != null;
        }
        final var result = getKassen().stream()
                .anyMatch(kasse -> kasse.getBezeichnung().equalsIgnoreCase(bezeichnung));
        getLogger().debug("isKasseExisting: result={}", result);
        return result;
    }

    /// Neue Kasse anlegen.
    /// @param kasse Zu erstellende Kasse
    /// @return Erstellte Kasse
    /// @throws KasseExistsException Es gibt bereits eine Kasse mit der Bezeichnung.
    public Kasse create(final Kasse kasse) {
        getLogger().debug("create: {}", kasse);
        kasse.setId(UUID.randomUUID());
        KASSEN.add(kasse);
        getLogger().debug("create: kasse={}", kasse);
        return kasse;
    }

    /// Vorhandene Kasse aktualisieren.
    /// @param kasse Kasse mit den neuen Daten (ohne ID)
    /// @param id ID der zu aktualisierenden Kasse
    /// @throws KasseExistsException Es gibt bereits eine Kasse mit der Bezeichnung.
    public void update(final Kasse kasse, final UUID id) {
        getLogger().debug("update: kasse={}, id={}", kasse, id);
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

        getLogger().debug("update: newKasse={}", newKasse);
        KASSEN.set(indexOfKasse, newKasse);
    }

    /// Vorhandene Kasse löschen.
    /// @param id Die ID der zu löschenden Kasse.
    public void delete(final UUID id) {
        getLogger().debug("delete: id={}", id);
        KASSEN.removeIf(kasse -> kasse.getId().equals(id));
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseRepository.class));
    }

}
