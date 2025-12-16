package com.acme.kms.controller;


import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import java.util.UUID;

@HttpExchange
interface KasseRepository {
    @GetExchange
    KasseOhneKassenbonsPage get(@RequestParam final MultiValueMap<String, String> suchparameter);

    @GetExchange("/{id}")
    ResponseEntity<KasseOhneKassenbons> getByIdOhneVersion(@PathVariable final String id);

    @PostExchange
    ResponseEntity<Void> post(final KasseDTO kasseDTO);

    @PutExchange("/{id}")
    ResponseEntity<Void> put(
            @PathVariable final UUID id,
            final KasseDTO kasseDTO,
            @RequestHeader("If-Match") final String ifMatch
    );

    @DeleteExchange("/{id}")
    ResponseEntity<Void> deleteById(@PathVariable final UUID id);
}
