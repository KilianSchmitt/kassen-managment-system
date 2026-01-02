/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.acme.kms.controller;

import java.io.Serial;
import org.springframework.http.HttpStatusCode;

/// Exception, falls die Versionsnummer im Request-Header bei `If-Match` fehlt oder syntaktisch ungültig ist.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
class VersionInvalidException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6132845254335875944L;

    private final HttpStatusCode status;

    /// Konstruktor für die Verwendung in KundeWriteController
    ///
    /// @param status HTTP-Statuscode.
    /// @param message Die eigentliche Meldung.
    VersionInvalidException(final HttpStatusCode status, final String message) {
        super(message);
        this.status = status;
    }

    /// Konstruktor für die Verwendung in KundeWriteController
    ///
    /// @param status HTTP-Statuscode.
    /// @param message Die eigentliche Meldung.
    /// @param ex Verursachende Exception
    VersionInvalidException(final HttpStatusCode status, final String message, final Exception ex) {
        super(message, ex);
        this.status = status;
    }

    @Override
    public String getMessage() {
        return super.getMessage() == null ? "" : super.getMessage();
    }

    HttpStatusCode getStatus() {
        return status;
    }
}
