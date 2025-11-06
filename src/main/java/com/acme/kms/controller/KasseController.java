package com.acme.kms.controller;


import com.acme.kms.entity.Kasse;
import com.acme.kms.service.KasseService;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(KasseController.API_PATH)
class KasseController {
    static final String API_PATH = "/kassen";
    private final KasseService service;

    KasseController(final KasseService service) {
        this.service = service;
    }

    @GetMapping(path = "{id}")
    Kasse getById(@PathVariable final UUID id) {
        return service.findById(id);
    }

    @GetMapping
    Collection<Kasse> get(@RequestParam final Map<String, String> queryparam) {
        return service.find(queryparam);
    }
}
