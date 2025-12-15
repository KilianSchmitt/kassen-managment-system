package com.acme.kms.controller;

import com.acme.kms.service.KasseService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.jspecify.annotations.Nullable;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

///// Eine Controller-Klasse bildet die REST-Schnittstelle, wobei die HTTP-Methoden, Pfade und MIME-Typen auf die
///// Methoden der Klasse abgebildet werden.
///// ![Klassendiagramm](/docs/asciidoc/KasseController.svg)
@RestController
@RequestMapping(KasseController.API_PATH)
@OpenAPIDefinition(info = @Info(title = "Kasse API"))
class KasseController {
    private static final String DEFAULT_KASSENBONS = "false";
    private static final String SUCHEN_TAG = "Suchen";

    static final String ID_PATTERN = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";

    static final String API_PATH = "/api/kassen";
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

    /// Suche mit diversen Query-Parameter.
    ///
    /// @param queryparam Query-Parameter als Map.
    /// @param page Seitennummerierung mit Spring Data.
    /// @param size Anzahl Einträge je Seite.
    /// @return Eine Response mit dem Statuscode 200 und den gefundenen Kassen als Page oder Statuscode 404.
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Suche mit Query-Parameter", tags = SUCHEN_TAG)
    @ApiResponse(responseCode = "200", description = "Page mit den Kassen")
    @ApiResponse(responseCode = "404", description = "Keine Kassen gefunden")
    PagedModel<KasseOhneKassenbons> get(
            @RequestParam final MultiValueMap<String, String> queryparam,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size
    ) {
        getLogger().debug("get: queryparam={}, page={}, size={}", queryparam, page, size);
        queryparam.remove("page");
        queryparam.remove("size");
        getLogger().trace("get: queryparam={}", queryparam);
        final var pageRequest = PageRequest.of(page, size);
        final var kassePage = service.find(queryparam, pageRequest).map(KasseOhneKassenbons::of);
        getLogger().debug("get: {}, {}", kassePage, kassePage.getContent());
        return new PagedModel<>(kassePage);
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseController.class));
    }
}
