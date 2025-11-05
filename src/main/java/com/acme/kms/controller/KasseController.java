package com.acme.kms.controller;

import com.acme.kms.entity.Kasse;
import com.acme.kms.service.KasseService;
import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(KasseController.API_PATH)
public class KasseController {
    static final String API_PATH = "/kassen";
    private final KasseService service;

    public KasseController(final KasseService service) {
        this.service = service;
    }

    @GetMapping
    Collection<Kasse> get() {
        return service.findAll();
    }
}
