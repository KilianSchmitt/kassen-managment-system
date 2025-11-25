package com.acme.kms.controller;

import com.acme.kms.entity.Kasse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

public interface KasseApiClient {
    @PostExchange
    ResponseEntity<Kasse> post(@RequestBody KasseDTO kasseDTO);

    @PutExchange(url = "/{id}")
    ResponseEntity<Void> put(@PathVariable String id, @RequestBody KasseDTO kasseDTO);

    @DeleteExchange(url = "/{id}")
    ResponseEntity<Void> delete(@PathVariable String id);
}
