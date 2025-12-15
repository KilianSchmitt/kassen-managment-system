package com.acme.kms.controller;

import com.acme.kms.service.KasseWriteService;
import com.acme.kms.controller.KasseDTO.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.groups.Default;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;
import java.net.URISyntaxException;

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
    ///
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


    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(KasseWriteController.class));
    }
}
