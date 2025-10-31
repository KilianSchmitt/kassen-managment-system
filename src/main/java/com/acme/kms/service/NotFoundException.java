package com.acme.kms.service;

import java.io.Serial;

public final class NotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1101909572340666200L;

    public NotFoundException() {
        super("Es wurde keine Kasse gefunden.");
    }
}
