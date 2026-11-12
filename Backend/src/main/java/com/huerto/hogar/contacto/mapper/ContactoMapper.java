package com.huerto.hogar.contacto.mapper;

import com.huerto.hogar.contacto.dto.ContactoDto;
import com.huerto.hogar.contacto.dto.ContactoResponse;
import com.huerto.hogar.contacto.entity.ContactoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/** Conversión automática DTO <-> entity de contacto (MapStruct genera la implementación). */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactoMapper {
    /** id, leido y creadoEn los fija la entidad (valores por defecto), no el cliente. */
    ContactoEntity toEntity(ContactoDto dto);

    ContactoResponse toDto(ContactoEntity entity);

    List<ContactoResponse> toDtoList(List<ContactoEntity> entities);
}
