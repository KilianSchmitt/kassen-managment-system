package com.acme.kms.service;

import java.io.Serial;

public final class NotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 123456788L;

    public NotFoundException() {
        super("Es wurde keine passende Kasse gefunden. Fehlercode: 404");
    }
}
