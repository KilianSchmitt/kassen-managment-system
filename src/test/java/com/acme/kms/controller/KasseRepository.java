package com.acme.kms.controller;


import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import java.util.UUID;

import static com.acme.kms.controller.TestConstants.VERSION_2;
import static org.springframework.http.HttpHeaders.IF_MATCH;

@HttpExchange
interface KasseRepository {
    @GetExchange
    KasseOhneKassenbonsPage get(@RequestParam final MultiValueMap<String, String> suchparameter);

    @GetExchange("/{id}")
    ResponseEntity<KasseOhneKassenbons> getByIdOhneVersion(@PathVariable final String id);

    @PostExchange(version = VERSION_2)
    ResponseEntity<Void> post(@RequestBody KasseDTO kasse);

    @PutExchange(url = "/{id}", version = VERSION_2)
    ResponseEntity<Void> put(
            @PathVariable String id,
            @RequestBody KasseDTO kunde,
            @RequestHeader(IF_MATCH) String version
    );

    @DeleteExchange(url = "/{id}", version = VERSION_2)
    ResponseEntity<Void> deleteById(@PathVariable String id);
}
