SET default_tablespace = kmsspace;

CREATE TABLE IF NOT EXISTS Kassierer (
     id              UUID PRIMARY KEY,
     vorname         TEXT NOT NULL,
     nachname        TEXT NOT NULL,
     email           TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Kasse (
    id                  UUID PRIMARY KEY,
    version             INTEGER NOT NULL DEFAULT 0,
    bezeichnung         TEXT NOT NULL,
    kassierer_id        UUID REFERENCES kassierer,
    bargeldbestand      NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS KassenBon (
    id         UUID PRIMARY KEY,
    date       TIMESTAMP NOT NULL,
    betrag     NUMERIC(10,2) NOT NULL,
    kasse_id   UUID REFERENCES Kasse,
    idx        INTEGER NOT NULL DEFAULT 0
);