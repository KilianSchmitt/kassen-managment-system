package com.acme.kms.controller;

import com.acme.kms.service.KasseExistsException;
import com.acme.kms.service.KasseWriteService;
import com.acme.kms.controller.KasseDTO.OnCreate;
import com.acme.kms.service.VersionOutdatedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.noContent;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static com.acme.kms.controller.KasseWriteController.API_PATH;

@Controller
@RequestMapping(API_PATH)
@Validated
public class KasseWriteController {
    static final String API_PATH = "/api/kassen";
    private static final String VERSIONSNUMMER_FEHLT = "Versionsnummer fehlt";

    private final KasseWriteService service;
    private final KasseMapper mapper;
    private final StableValue<Logger> logger = StableValue.of();

    KasseWriteController(KasseWriteService service, KasseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /// Einen neuen Kassen-Datensatz anlegen.
    /// @param kasseDTO Das Kassenobjekt mit den Daten aus dem eingehenden Request-Body.
    /// @param request Das Request-Objekt, um den Location-Header im Response zu erstellen.
    /// @return Response mit Statuscode 201 einschließlich Location-Header oder Statuscode 422, falls Constraints
    ///      verletzt sind oder die Kassenbezeichnung bereits existiert, oder Statuscode 400 bei syntaktischen Fehlern im
    ///      Request-Body.
    /// @throws URISyntaxException falls die URI im Request-Objekt nicht korrekt ist
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Eine neue Kasse anlegen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Kasse neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Kassenbezeichnung vorhanden")
    public ResponseEntity<Void> post(
            @RequestBody @Validated({Default.class, OnCreate.class}) final KasseDTO kasseDTO,
            final HttpServletRequest request
    ) throws URISyntaxException {
        getLogger().debug("post: kasseDTO={}", kasseDTO);
        final var kasseInput = mapper.toKasse(kasseDTO);
        final var kasse = service.create(kasseInput);
        final var baseUri = request.getRequestURL().toString();
        final var location = new URI(baseUri + "/" + kasse.getId());
        return ResponseEntity.created(location).build();
    }

    /**
     * Einen vorhandenen Kassen-Datensatz überschreiben.
     * @param id ID der zu aktualisierenden Kasse.
     * @param kasseDTO Das Kassenobjekt aus dem eingegangenen Request-Body.
     * @param ifMatch Versionsnummer aus dem Header If-Match
     * @return Response mit Statuscode 204 oder Statuscode 400, falls der JSON-Datensatz syntaktisch nicht korrekt ist
     *      oder 422, falls Constraints verletzt sind oder die Kassenbezeichnung bereits existiert
     *      oder 412, falls die Versionsnummer nicht ok ist
     *      oder 428, falls die Versionsnummer fehlt.
     */
    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Eine Kasse mit neuen Werten aktualisieren", tags = "Aktualisieren")
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "404", description = "Kasse nicht vorhanden")
    @ApiResponse(responseCode = "412", description = "Versionsnummer falsch")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Kassenbezeichnung vorhanden")
    @ApiResponse(responseCode = "428", description = VERSIONSNUMMER_FEHLT)
    public ResponseEntity<Void> put(
            @PathVariable final UUID id,
            @RequestBody @Validated final KasseDTO kasseDTO,
            @RequestHeader("If-Match") @Nullable final String ifMatch
    ) {
        getLogger().debug("put: id={}, kasseDTO={}, ifMatch={}", id, kasseDTO, ifMatch);
        final int version = getVersion(ifMatch);
        final var kasseInput = mapper.toKasse(kasseDTO);
        final var kasse = service.update(kasseInput, id, version);
        getLogger().debug("put: {}", kasse);
        return noContent().eTag("\"" + kasse.getVersion() + '"').build();
    }

    @SuppressWarnings({"MagicNumber", "RedundantSuppression"})
    private int getVersion(@Nullable final String versionStr) {
        getLogger().trace("getVersion: {}", versionStr);
        if (versionStr == null) {
            throw new VersionInvalidException(PRECONDITION_REQUIRED, VERSIONSNUMMER_FEHLT);
        }
        if (versionStr.length() < 3 ||
                versionStr.charAt(0) != '"' ||
                versionStr.charAt(versionStr.length() - 1) != '"') {
            throw new VersionInvalidException(PRECONDITION_FAILED, "Ungueltiges ETag " + versionStr);
        }

        final int version;
        try {
            version = Integer.parseInt(versionStr.substring(1, versionStr.length() - 1));
        } catch (final NumberFormatException ex) {
            throw new VersionInvalidException(PRECONDITION_FAILED, "Ungueltiges ETag " + versionStr, ex);
        }

        getLogger().trace("getVersion: version={}", version);
        return version;
    }

    /// Einen vorhandenen Kunden anhand seiner ID löschen.
    /// @param id ID des zu löschenden Kunden.
    @DeleteMapping(path = "{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Eine Kasse anhand der ID loeschen", tags = "Loeschen")
    @ApiResponse(responseCode = "204", description = "Gelöscht")
    void deleteById(@PathVariable final UUID id)  {
        getLogger().debug("deleteById: id={}", id);
        service.deleteById(id);
    }

    /// _Exception Handler_ für _Spring WebMvc_ falls Constraints bei _POST_- oder _PUT_-Requests verletzt sind.
    /// @param ex Exception vom Typ `MethodArgumentNotValidException`.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onConstraintViolations(final MethodArgumentNotValidException ex) {
        getLogger().debug("onConstraintViolations: {}", ex.getMessage());

        final var detailMessages = ex.getDetailMessageArguments();
        final var detail = detailMessages.length == 0 || detailMessages[1] == null
                ? "Constraint Violation"
                : ((String) detailMessages[1]).replace(", and ", ", ");
        return ErrorResponse.create(ex, UNPROCESSABLE_CONTENT, detail);
    }

    /// _ExceptionHandler_, falls bei einem _POST_- oder _PUT_-Request eine neue E-Mail bereits existiert.
    /// @param ex Exception vom Typ `MethodArgumentNotValidException`.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onKasseExists(final KasseExistsException ex) {
        getLogger().debug("onKasseExists: {}", ex.getMessage());
        final var detailMessages = ex.getMessage() != null ? ex.getMessage() : "Kassenbezeichnung existiert bereits";
        return ErrorResponse.create(ex, UNPROCESSABLE_CONTENT, detailMessages);
    }

    /// _ExceptionHandler_, falls bei einem _PUT_-Request die Version fehlerhaft ist.
    /// @param ex Exception vom Typ `VersionInvalidException`.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onVersionInvalid(final VersionInvalidException ex) {
        getLogger().debug("onVersionInvalid: {}", ex.getMessage());
        return ErrorResponse.create(ex, ex.getStatus(), ex.getMessage());
    }

    /// _ExceptionHandler_, falls bei einem _PUT_-Request die Version nicht aktuell existiert.
    /// @param ex Exception vom Typ `VersionOutdatedException`.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onVersionOutdated(final VersionOutdatedException ex) {
        getLogger().debug("onVersionOutdated: {}", ex.getMessage());
        final var detailMessage = ex.getMessage() != null ? ex.getMessage() : "Versionsnummer veraltet";
        return ErrorResponse.create(ex, PRECONDITION_FAILED, detailMessage);
    }

    /// _ExceptionHandler_, falls bei einem _POST_- oder _PUT_-Request der Request-Body syntaktisch falsch ist.
    /// @param ex Exception vom Typ `HttpMessageNotReadableException`.
    /// @return ErrorResponse mit `ProblemDetail` gemäß _RFC 9457_.
    @ExceptionHandler
    ErrorResponse onMessageNotReadable(final HttpMessageNotReadableException ex) {
        final var msg = ex.getMessage() == null ? "N/A" : ex.getMessage();
        getLogger().debug("onMessageNotReadable: {}", msg);
        return ErrorResponse.create(ex, BAD_REQUEST, msg);
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseWriteController.class));
    }
}
