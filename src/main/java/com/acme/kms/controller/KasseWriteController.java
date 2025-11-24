package com.acme.kms.controller;

import com.acme.kms.service.KasseWriteService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.groups.Default;
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

    KasseWriteController(final KasseWriteService service, final KasseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Kasse neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "409", description = "Ungültige Werte oder Kasse vorhanden")
    ResponseEntity<Void> post(
            @RequestBody @Validated({Default.class, KasseDTO.OnCreate.class}) final KasseDTO kasseDTO,
            final HttpServletRequest request
    ) {
        final var kasseInput = mapper.toKasse(kasseDTO);
        final var kasse = service.create(kasseInput);
        final URI location = URI.create(request.getRequestURL().toString() + "/" + kasse.getId());
        return created(location).build();
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    void put(@PathVariable final UUID id,
             @RequestBody @Validated final KasseDTO kasseDTO
    ) {
        service.update(mapper.toKasse(kasseDTO), id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "Gelöscht")
    void deleteByID(@PathVariable final UUID id) {
        service.delete(id);
    }

}
