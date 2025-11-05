package com.acme.kms.entity;

import java.util.UUID;

public class Kassierer {
    private UUID id;
    private String name;

    public Kassierer(final UUID id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Kassierer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
