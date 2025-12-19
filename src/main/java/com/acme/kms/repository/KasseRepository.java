package com.acme.kms.repository;

import com.acme.kms.entity.Kasse;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

import static com.acme.kms.entity.Kasse.KASSIERER_GRAPH;
import static com.acme.kms.entity.Kasse.KASSIERER_KASSENBONS_GRAPH;

public interface KasseRepository extends JpaRepository<Kasse, UUID>, JpaSpecificationExecutor<Kasse> {
    @EntityGraph(KASSIERER_GRAPH)
    @Override
    Page<Kasse> findAll(Pageable pageable);

    @EntityGraph(KASSIERER_GRAPH)
    @Override
    Page<Kasse> findAll(@Nullable Specification<Kasse> spec, Pageable pageable);

    @Query("""
        SELECT kasse
        FROM   #{#entityName} kasse
        WHERE  kasse.id = :id
        """)
    @EntityGraph(KASSIERER_GRAPH)
    @Nullable
    Kasse findByIdFetchKassierer(UUID id);

    @Query("""
        SELECT kasse
        FROM   #{#entityName} kasse
        WHERE  kasse.id = :id
        """)
    @EntityGraph(KASSIERER_KASSENBONS_GRAPH)
    @Nullable
    Kasse findByIdFetchKassiererUndKassenbons(UUID id);

    @Query("""
        SELECT kasse
        FROM   #{#entityName} kasse
        WHERE  lower(kasse.bezeichnung) LIKE concat(lower(:bezeichnung), '%')
        """)
    @EntityGraph(KASSIERER_GRAPH)
    @Nullable
    Page<Kasse> findByBezeichnung(String bezeichnung, Pageable pageable);

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    boolean existsByBezeichnung(String bezeichnung);

    @Query("""
        SELECT   kasse
        FROM     #{#entityName} kasse
        WHERE    lower(kasse.kassierer.vorname) LIKE concat(lower(:kassiererName), '%')
               OR lower(kasse.kassierer.nachname) LIKE concat(lower(:kassiererName), '%')
        """)
    Page<Kasse> findByKassiererName(String kassiererName, Pageable pageable);
}
