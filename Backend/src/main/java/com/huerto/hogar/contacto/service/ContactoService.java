package com.huerto.hogar.contacto.service;

import com.huerto.hogar.contacto.entity.ContactoEntity;
import com.huerto.hogar.contacto.interfaces.IContactoService;
import com.huerto.hogar.contacto.repository.ContactoRepository;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ContactoService implements IContactoService {

    private static final Pattern CORREO_PERMITIDO = Pattern.compile(
        "^[a-z0-9._%+\\-]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$");

    private final ContactoRepository repo;

    @Override
    public ContactoEntity save(ContactoEntity c) {
        String correo = c.getCorreo() == null ? "" : c.getCorreo().trim().toLowerCase(Locale.ROOT);
        if (!CORREO_PERMITIDO.matcher(correo).matches())
            throw new ReglaDeNegocioException(
                "El correo debe ser @duoc.cl, @profesor.duoc.cl o @gmail.com");
        c.setCorreo(correo);
        c.setNombre(c.getNombre().trim());
        c.setComentario(c.getComentario().trim());
        c.setLeido(false);
        return repo.save(c);
    }

    @Override
    public List<ContactoEntity> findAll() {
        return repo.findAllByOrderByCreadoEnDesc();
    }

    @Override
    public ContactoEntity marcarLeido(Long id) {
        ContactoEntity c = repo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Mensaje de contacto no encontrado: " + id));
        c.setLeido(true);
        return repo.save(c);
    }
}
