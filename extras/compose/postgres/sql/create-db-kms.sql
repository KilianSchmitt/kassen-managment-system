CREATE USER kms PASSWORD 'p';

CREATE DATABASE kms;

GRANT ALL ON DATABASE kms TO kms;

CREATE TABLESPACE kmsspace OWNER kms LOCATION '/var/lib/postgresql/tablespace/kms';