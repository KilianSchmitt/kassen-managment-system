package com.acme.kms.service;

import java.io.Serial;

public class KasseExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 123456789L;

    public KasseExistsException(final String bezeichnung) {
        super("Die Kasse mit der Bezeichnung '" + bezeichnung + "' existiert bereits.");
    }

    @Override
    public String getMessage() {
        return super.getMessage() == null ? "Die Kasse existiert bereits." : super.getMessage();
    }
}
