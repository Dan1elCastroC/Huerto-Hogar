package com.huerto.hogar.contacto.interfaces;

import com.huerto.hogar.contacto.entity.ContactoEntity;
import java.util.List;

public interface IContactoService {
    ContactoEntity save(ContactoEntity contacto);
    List<ContactoEntity> findAll();
    ContactoEntity marcarLeido(Long id);
}
