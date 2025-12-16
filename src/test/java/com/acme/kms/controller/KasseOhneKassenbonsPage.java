package com.acme.kms.controller;

import org.springframework.data.web.PagedModel;

import java.util.Collections;
import java.util.List;

public record KasseOhneKassenbonsPage(
        List<KasseOhneKassenbons> content,
        PagedModel.PageMetadata page
) {
    public KasseOhneKassenbonsPage(
            final List<KasseOhneKassenbons> content,
            final PagedModel.PageMetadata page
    ) {
        this.content = Collections.unmodifiableList(content);
        this.page = page;
    }

    @Override
    public List<KasseOhneKassenbons> content() {
        return Collections.unmodifiableList(content);
    }
}
