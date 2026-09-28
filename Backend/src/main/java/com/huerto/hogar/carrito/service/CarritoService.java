package com.huerto.hogar.carrito.service;

import com.huerto.hogar.carrito.dto.CarritoItemDto;
import com.huerto.hogar.carrito.dto.CarritoResponse;
import com.huerto.hogar.carrito.entity.CarritoItemEntity;
import com.huerto.hogar.carrito.interfaces.ICarritoService;
import com.huerto.hogar.carrito.repository.CarritoRepository;
import com.huerto.hogar.cupon.entity.CuponEntity;
import com.huerto.hogar.cupon.interfaces.ICuponService;
import com.huerto.hogar.exception.*;
import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.producto.repository.ProductoRepository;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarritoService implements ICarritoService {

    private final CarritoRepository carritoRepo;
    private final ProductoRepository productoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ICuponService cuponService;

    @Override
    public CarritoResponse getCarrito(String correo, String codigoCupon) {
        UsuarioEntity usuario = getUsuario(correo);
        List<CarritoItemEntity> items = carritoRepo.findByUsuarioId(usuario.getId());
        return buildResponse(items, codigoCupon);
    }

    @Override
    @Transactional
    public CarritoResponse agregar(String correo, CarritoItemDto dto) {
        UsuarioEntity usuario = getUsuario(correo);
        ProductoEntity producto = productoRepo.findById(dto.getProductoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        if (!producto.isActivo())
            throw new ReglaDeNegocioException("El producto no está disponible");
        if (producto.getStock() < dto.getCantidad())
            throw new ReglaDeNegocioException("Stock insuficiente. Disponible: " + producto.getStock());

        Optional<CarritoItemEntity> existente =
            carritoRepo.findByUsuarioIdAndProductoId(usuario.getId(), producto.getId());

        if (existente.isPresent()) {
            // Actualizar cantidad sumando
            CarritoItemEntity item = existente.get();
            int nuevaCantidad = item.getCantidad() + dto.getCantidad();
            if (producto.getStock() < nuevaCantidad)
                throw new ReglaDeNegocioException("Stock insuficiente para esa cantidad");
            item.setCantidad(nuevaCantidad);
            carritoRepo.save(item);
        } else {
            carritoRepo.save(CarritoItemEntity.builder()
                .usuario(usuario)
                .producto(producto)
                .cantidad(dto.getCantidad())
                .precioUnitario(producto.getPrecio())
                .build());
        }
        return buildResponse(carritoRepo.findByUsuarioId(usuario.getId()), null);
    }

    @Override
    @Transactional
    public CarritoResponse actualizar(String correo, Long productoId, Integer cantidad) {
        UsuarioEntity usuario = getUsuario(correo);
        CarritoItemEntity item = carritoRepo
            .findByUsuarioIdAndProductoId(usuario.getId(), productoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Item no encontrado en el carrito"));

        if (cantidad <= 0) {
            carritoRepo.delete(item);
        } else {
            if (item.getProducto().getStock() < cantidad)
                throw new ReglaDeNegocioException("Stock insuficiente. Disponible: " + item.getProducto().getStock());
            item.setCantidad(cantidad);
            carritoRepo.save(item);
        }
        return buildResponse(carritoRepo.findByUsuarioId(usuario.getId()), null);
    }

    @Override
    @Transactional
    public CarritoResponse eliminarItem(String correo, Long productoId) {
        UsuarioEntity usuario = getUsuario(correo);
        carritoRepo.findByUsuarioIdAndProductoId(usuario.getId(), productoId)
            .ifPresent(carritoRepo::delete);
        return buildResponse(carritoRepo.findByUsuarioId(usuario.getId()), null);
    }

    @Override
    @Transactional
    public void vaciar(String correo) {
        UsuarioEntity usuario = getUsuario(correo);
        carritoRepo.deleteByUsuarioId(usuario.getId());
    }

    // ── helpers ──────────────────────────────────────────────────────

    private UsuarioEntity getUsuario(String correo) {
        return usuarioRepo.findByCorreo(correo)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    private CarritoResponse buildResponse(List<CarritoItemEntity> items, String codigoCupon) {
        List<CarritoResponse.ItemResponse> itemResponses = items.stream()
            .map(i -> CarritoResponse.ItemResponse.builder()
                .id(i.getId())
                .productoId(i.getProducto().getId())
                .productoNombre(i.getProducto().getNombre())
                .productoImagenUrl(i.getProducto().getImagenUrl())
                .cantidad(i.getCantidad())
                .precioUnitario(i.getPrecioUnitario())
                .subtotal(i.getSubtotal())
                .build())
            .toList();

        BigDecimal subtotal = items.stream()
            .map(CarritoItemEntity::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = BigDecimal.ZERO;
        String cuponAplicado = null;

        if (codigoCupon != null && !codigoCupon.isBlank()) {
            try {
                CuponEntity cupon = cuponService.findByCodigo(codigoCupon);
                descuento = subtotal
                    .multiply(cupon.getPorcentaje())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                cuponAplicado = cupon.getCodigo();
            } catch (Exception ignored) {
                // cupón inválido: se ignora silenciosamente
            }
        }

        return CarritoResponse.builder()
            .items(itemResponses)
            .totalItems(items.stream().mapToInt(CarritoItemEntity::getCantidad).sum())
            .subtotal(subtotal)
            .descuento(descuento)
            .total(subtotal.subtract(descuento))
            .cuponAplicado(cuponAplicado)
            .build();
    }
}
