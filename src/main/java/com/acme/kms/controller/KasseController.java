package com.acme.kms.controller;

import com.acme.kms.entity.Kasse;
import com.acme.kms.service.KasseService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/// Eine Controller-Klasse bildet die REST-Schnittstelle, wobei die HTTP-Methoden, Pfade und MIME-Typen auf die
/// Methoden der Klasse abgebildet werden.
/// ![Klassendiagramm](/docs/asciidoc/KasseController.svg)
@RestController
@RequestMapping(KasseController.API_PATH)
@OpenAPIDefinition(info = @Info(title = "Kasse API"))
class KasseController {
    private final StableValue<Logger> logger = StableValue.of();
    static final String API_PATH = "/kassen";
    private final KasseService service;

    /// Konstruktor mit _package private_ für _Spring_.
    /// @param service Injiziertes Service-Objekt.
    KasseController(final KasseService service) {
        this.service = service;
    }

    /// Suche anhand der Kassen-ID als Pfad-Parameter.
    /// @param id ID der zu suchenden Kasse
    /// @return Gefundene Kasse.
    @Operation(summary = "Suche mit der Kassen-ID", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "Kasse gefunden")
    @ApiResponse(responseCode = "404", description = "Kasse nicht gefunden")
    @GetMapping(path = "{id}")
    Kasse getById(@PathVariable final UUID id) {
        getLogger().debug("Getting Kasse with id {}", id);
        return service.findById(id);
    }

    /// Kassen anhand von Suchparametern als Collection suchen.
    /// @param queryparam Query-Parameter als Map.
    /// @return Gefundene Kassen als [Collection].
    @GetMapping
    @Operation(summary = "Suche mit Query-Parameter", tags = "kassiererName")
    @ApiResponse(responseCode = "200", description = "Collection mit den Kassen")
    @ApiResponse(responseCode = "404", description = "Keine Kasse gefunden")
    Collection<Kasse> get(@RequestParam final Map<String, String> queryparam) {
        getLogger().debug("Getting Kassen with queryparam {}", queryparam);
        return service.find(queryparam);
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseController.class));
    }
}
