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
package com.acme.kms.mail;

import com.acme.kms.entity.Kasse;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/// Mail-Client.
///
/// @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
@Service
@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class MailService {
    private final StableValue<Logger> logger = StableValue.of();

    /// Objekt für _Jakarta Mail_, um E-Mails zu verschicken
    private final JavaMailSender mailSender;

    /// Mailserver
    @Value("${spring.mail.host}")
    @SuppressWarnings("NullAway.Init")
    private String mailhost;

    /// Konstruktor mit `package private` für _Constructor Injection_ bei _Spring_.
    ///
    /// @param mailSender Injiziertes Objekt für _Spring Mail_.
    MailService(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /// E-Mail senden, dass es eine neue Kasse gibt.
    ///
    /// @param neueKasse Das Objekt der neuen Kasse.
    @Async
    public void send(final Kasse neueKasse) {
        final var mimeMessage = mailSender.createMimeMessage();

        try {
            final var mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom("Theo Test <theo@test.de>");
            mimeMessageHelper.setTo("Maxi Musterfrau <KilianSchmitt@test.com>");
            mimeMessageHelper.setSubject("Neue Kasse " + neueKasse.getId());
            final var plainText = "Neue Kasse: " + neueKasse.getBezeichnung();
            final var htmlText = "<strong>Neuer Kasse:</strong> <em>" + neueKasse.getBezeichnung() + "</em>";
            mimeMessageHelper.setText(plainText, htmlText);

            mailSender.send(mimeMessage);
            getLogger().trace("send: Thread-ID={}, mailConfig={}, kasse={}",
                Thread.currentThread().threadId(), mailhost, neueKasse);
        } catch (MailException | MessagingException _) {
            getLogger().warn("Email nicht gesendet: Ist der Mailserver {} erreichbar?", mailhost);
        }
    }

    private Logger getLogger() {
        return logger.orElseSet(() -> LoggerFactory.getLogger(MailService.class));
    }
}
