package com.acme.kms.controller;

import com.acme.kms.entity.Kasse;
import com.acme.kms.entity.KassenBon;
import com.acme.kms.entity.Kassierer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface KasseMapper {
    @Mapping(target = "id", ignore = true)
    Kasse toKasse(KasseDTO dto);
    @Mapping(target = "id", ignore = true)
    Kassierer toKassierer(KassiererDTO dto);
    @Mapping(target = "id", ignore = true)
    KassenBon toKassenBon(KassenBonDTO dto);
}
