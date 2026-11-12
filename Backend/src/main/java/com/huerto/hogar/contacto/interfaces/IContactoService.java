package com.huerto.hogar.contacto.interfaces;

import com.huerto.hogar.contacto.dto.ContactoDto;
import com.huerto.hogar.contacto.dto.ContactoResponse;

import java.util.List;

public interface IContactoService {
    ContactoResponse save(ContactoDto dto);
    List<ContactoResponse> findAll();
    ContactoResponse marcarLeido(Long id);
}
