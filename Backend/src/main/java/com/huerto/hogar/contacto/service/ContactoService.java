package com.huerto.hogar.contacto.service;

import com.huerto.hogar.contacto.dto.ContactoDto;
import com.huerto.hogar.contacto.dto.ContactoResponse;
import com.huerto.hogar.contacto.entity.ContactoEntity;
import com.huerto.hogar.contacto.interfaces.IContactoService;
import com.huerto.hogar.contacto.mapper.ContactoMapper;
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
    private final ContactoMapper mapper;

    @Override
    public ContactoResponse save(ContactoDto dto) {
        // DTO -> entity: solo la entity sabe guardarse en la base mediante el repositorio.
        ContactoEntity c = mapper.toEntity(dto);
        String correo = c.getCorreo() == null ? "" : c.getCorreo().trim().toLowerCase(Locale.ROOT);
        if (!CORREO_PERMITIDO.matcher(correo).matches())
            throw new ReglaDeNegocioException(
                "El correo debe ser @duoc.cl, @profesor.duoc.cl o @gmail.com");
        c.setCorreo(correo);
        c.setNombre(c.getNombre().trim());
        c.setComentario(c.getComentario().trim());
        c.setLeido(false);
        // entity -> DTO: la entity nunca sale del servicio.
        return mapper.toDto(repo.save(c));
    }

    @Override
    public List<ContactoResponse> findAll() {
        return mapper.toDtoList(repo.findAllByOrderByCreadoEnDesc());
    }

    @Override
    public ContactoResponse marcarLeido(Long id) {
        ContactoEntity c = repo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Mensaje de contacto no encontrado: " + id));
        c.setLeido(true);
        return mapper.toDto(repo.save(c));
    }
}
