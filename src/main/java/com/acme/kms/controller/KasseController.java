package com.acme.kms.controller;
//
//import com.acme.kms.entity.Kasse;
//import com.acme.kms.service.KasseService;
//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.info.Info;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.*;
//import java.util.Collection;
//import java.util.Map;
//import java.util.UUID;
//

import com.acme.kms.entity.Kasse;
import com.acme.kms.service.KasseService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

///// Eine Controller-Klasse bildet die REST-Schnittstelle, wobei die HTTP-Methoden, Pfade und MIME-Typen auf die
///// Methoden der Klasse abgebildet werden.
///// ![Klassendiagramm](/docs/asciidoc/KasseController.svg)
//@RestController
//@RequestMapping(KasseController.API_PATH)
//@OpenAPIDefinition(info = @Info(title = "Kasse API"))
//class KasseController {
//    private final StableValue<Logger> logger = StableValue.of();
//    static final String API_PATH = "/kassen";
//    private final KasseService service;
//
//    /// Konstruktor mit _package private_ für _Spring_.
//    /// @param service Injiziertes Service-Objekt.
//    KasseController(final KasseService service) {
//        this.service = service;
//    }
//
//    /// Suche anhand der Kassen-ID als Pfad-Parameter.
//    /// @param id ID der zu suchenden Kasse
//    /// @return Gefundene Kasse.
//    @Operation(summary = "Suche mit der Kassen-ID", tags = "Suchen")
//    @ApiResponse(responseCode = "200", description = "Kasse gefunden")
//    @ApiResponse(responseCode = "404", description = "Kasse nicht gefunden")
//    @GetMapping(path = "{id}")
//    Kasse getById(@PathVariable final UUID id) {
//        getLogger().debug("Getting Kasse with id {}", id);
//        return service.findById(id);
//    }
//
//    /// Kassen anhand von Suchparametern als Collection suchen.
//    /// @param queryparam Query-Parameter als Map.
//    /// @return Gefundene Kassen als [Collection].
//    @GetMapping
//    @Operation(summary = "Suche mit Query-Parameter", tags = "kassiererName")
//    @ApiResponse(responseCode = "200", description = "Collection mit den Kassen")
//    @ApiResponse(responseCode = "404", description = "Keine Kasse gefunden")
//    Collection<Kasse> get(@RequestParam final Map<String, String> queryparam) {
//        getLogger().debug("Getting Kassen with queryparam {}", queryparam);
//        return service.find(queryparam);
//    }
//
//    private Logger getLogger() {
//        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseController.class));
//    }
//}

@RestController
@RequestMapping(KasseController.API_PATH)
@OpenAPIDefinition(info = @Info(title = "Kasse API"))
class KasseController {
    private static final String DEFAULT_KASSENBONS = "false";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "5";
    private static final String BEZEICHUNG_PATH = "/bezeichnung";
    private static final String SUCHEN_TAG = "Suchen";

    static final String ID_PATTERN = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";

    static final String API_PATH = "/kassen";
    private final KasseService service;
    private final StableValue<Logger> logger = StableValue.of();

    KasseController(final KasseService service) {
        this.service = service;
    }

    /// Suche anhand der Kassen-ID als Pfad-Parameter.
    ///
    /// @param id ID der zu suchenden Kasse
    /// @param kassenbons Flag, ob KassenBons mitgeladen werden (default: false)
    /// @param ifNoneMatch Versionsnummer aus dem Header If-None-Match
    /// @return Eine Response mit Statuscode 200 und der gefundenen Kasse oder 404.
    @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Suche mit der Kassen-ID", tags = SUCHEN_TAG)
    @Parameter(name = "If-None-Match", in = ParameterIn.HEADER)
    @ApiResponse(responseCode = "200", description = "Kasse gefunden")
    @ApiResponse(responseCode = "404", description = "Kasse nicht gefunden")
    @SuppressWarnings("ReturnCount")
    ResponseEntity<Object> getById(
            @PathVariable final UUID id,
            @RequestParam(defaultValue = DEFAULT_KASSENBONS) final boolean kassenbons,
            @RequestHeader("If-None-Match") @Nullable final String ifNoneMatch
    ) {
        getLogger().debug("getById: id={}", id);

        if (kassenbons) {
            return getByIdMitKassiererUndKassenbons(id, ifNoneMatch);
        }
        return getByIdMitKassierer(id, ifNoneMatch);
    }

    private ResponseEntity<Object> getByIdMitKassierer(
            final UUID id,
            @Nullable final String ifNoneMatch
    ) {
        getLogger().trace("getByIdMitKassierer: id={}, ifNoneMatch={}", id, ifNoneMatch);

        final var kasse = service.findByIdMitKassierer(id);
        if (kasse == null) {
            return notFound().build();
        }
        final var versionStr = "\"" + kasse.getVersion() + "\"";
        if (versionStr.equals(ifNoneMatch)) {
            return status(NOT_MODIFIED).build();
        }

        // DTO-Objekt ohne KassenBons
        return ok().eTag(versionStr).body(KasseOhneKassenbons.of(kasse));
    }

    private ResponseEntity<Object> getByIdMitKassiererUndKassenbons(
            final UUID id,
            @Nullable final String ifNoneMatch
    ) {
        getLogger().trace("getByIdMitKassiererUndKassenbons: id={}, ifNoneMatch={}", id, ifNoneMatch);

        final var kasse = service.findByIdMitKassiererUndKassenbons(id);
        if (kasse == null) {
            return notFound().build();
        }
        final var versionStr = "\"" + kasse.getVersion() + "\"";
        if (versionStr.equals(ifNoneMatch)) {
            return status(NOT_MODIFIED).build();
        }

        return ok().eTag(versionStr).body(kasse);
    }


    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseController.class));
    }
}
