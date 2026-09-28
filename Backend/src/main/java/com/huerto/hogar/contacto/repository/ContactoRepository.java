package com.huerto.hogar.contacto.repository;

import com.huerto.hogar.contacto.entity.ContactoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactoRepository extends CrudRepository<ContactoEntity, Long> {
    List<ContactoEntity> findAllByOrderByCreadoEnDesc();
}
