package com.acme.kms.service;

public final class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Es wurde keine Kasse gefunden. Fehlercode: 404");
    }
}
