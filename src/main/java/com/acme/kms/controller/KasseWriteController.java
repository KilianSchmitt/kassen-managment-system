package com.acme.kms.controller;

import com.acme.kms.service.KasseWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.created;

@RestController
@Validated
@RequestMapping("/kassen")
class KasseWriteController {
    private final KasseWriteService service;
    private final KasseMapper mapper;
    private final StableValue<Logger> logger = StableValue.of();

    /// Konstruktor mit _package private_ für _Spring_.
    /// @param service Injiziertes Service-Objekt.
    /// @param mapper Injiziertes Mapper-Objekt für das Mapping von DTO-Objekten auf Entity-Objekte.
    KasseWriteController(final KasseWriteService service, final KasseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /// Einen neuen Kassen-Datensatz anlegen.
    /// @param kasseDTO Das Kassenobjekt aus dem eingegangenen Request-Body.
    /// @param request Das Request-Objekt, um `Location` im Response-Header zu erstellen.
    /// @return Response mit Statuscode `201` einschließlich Location-Header oder Statuscode `422`, falls Constraints
    /// verletzt sind oder die Kasse bereits existiert oder Statuscode `409`, falls syntaktische Fehler im
    /// Request-Body vorliegen.
    @PostMapping
    @Operation(summary = "Eine neue Kasse anlegen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Kasse neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "409", description = "Kasse vorhanden")
    ResponseEntity<Void> post(
            @RequestBody @Validated({Default.class, KasseDTO.OnCreate.class}) final KasseDTO kasseDTO,
            final HttpServletRequest request
    ) {
        getLogger().debug("Creating Kasse {}", kasseDTO);
        final var kasseInput = mapper.toKasse(kasseDTO);
        final var kasse = service.create(kasseInput);
        final URI location = URI.create(request.getRequestURL().toString() + "/" + kasse.getId());
        return created(location).build();
    }

    /// Einen vorhandenen Kassen-Datensatz überschreiben.
    /// @param id ID der zu aktualisierenden Kasse.
    /// @param kasseDTO Das Kassenobjekt aus dem eingegangenen Request-Body.
    @PutMapping(path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Eine Kasse mit neuen Werten aktualisieren", tags = "Aktualisieren")
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "404", description = "Kasse nicht vorhanden")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Kassenbezeichnung vorhanden")
    void put(@PathVariable final UUID id,
             @RequestBody @Validated final KasseDTO kasseDTO
    ) {
        getLogger().debug("Updating Kasse {}", kasseDTO);
        service.update(mapper.toKasse(kasseDTO), id);
    }

    /// Einen vorhandenen Kassen-Datensatz löschen.
    /// @param id Die ID der zu löschenden Kasse.
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Einen Kunden anhand der ID loeschen", tags = "Loeschen")
    @ApiResponse(responseCode = "204", description = "Gelöscht")
    void deleteByID(@PathVariable final UUID id) {
        getLogger().debug("Deleting Kasse {}", id);
        service.delete(id);
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseWriteController.class));
    }
}
