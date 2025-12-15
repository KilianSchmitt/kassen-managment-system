SET default_tablespace = kmsspace;

CREATE INDEX IF NOT EXISTS kasse_kassierer_id_idx ON kasse(kassierer_id);
CREATE INDEX IF NOT EXISTS kassenbon_kasse_id_idx ON kassenbon(kasse_id);
CREATE INDEX IF NOT EXISTS kassierer_nachname_idx ON kassierer(nachname);
