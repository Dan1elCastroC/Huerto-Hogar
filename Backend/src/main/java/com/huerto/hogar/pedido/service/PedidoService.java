package com.huerto.hogar.pedido.service;

import com.huerto.hogar.carrito.entity.CarritoItemEntity;
import com.huerto.hogar.carrito.interfaces.ICarritoService;
import com.huerto.hogar.carrito.repository.CarritoRepository;
import com.huerto.hogar.cupon.entity.CuponEntity;
import com.huerto.hogar.cupon.interfaces.ICuponService;
import com.huerto.hogar.exception.*;
import com.huerto.hogar.pedido.dto.CheckoutRequest;
import com.huerto.hogar.pedido.dto.PedidoResponse;
import com.huerto.hogar.pedido.entity.*;
import com.huerto.hogar.pedido.interfaces.IPedidoService;
import com.huerto.hogar.pedido.repository.PedidoRepository;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService implements IPedidoService {

    private final PedidoRepository pedidoRepo;
    private final CarritoRepository carritoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ICuponService cuponService;
    private final ICarritoService carritoService;

    @Override
    @Transactional
    public PedidoResponse checkout(String correo, CheckoutRequest req) {
        UsuarioEntity usuario = getUsuario(correo);
        List<CarritoItemEntity> items = carritoRepo.findByUsuarioId(usuario.getId());

        if (items.isEmpty())
            throw new ReglaDeNegocioException("El carrito está vacío");

        // Verificar stock de cada item antes de confirmar
        items.forEach(item -> {
            if (item.getProducto().getStock() < item.getCantidad())
                throw new ReglaDeNegocioException(
                    "Stock insuficiente para: " + item.getProducto().getNombre() +
                    ". Disponible: " + item.getProducto().getStock());
        });

        // Calcular subtotal
        BigDecimal subtotal = items.stream()
            .map(CarritoItemEntity::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aplicar cupón si existe
        BigDecimal descuento = BigDecimal.ZERO;
        String cuponAplicado = null;
        if (req.getCodigoCupon() != null && !req.getCodigoCupon().isBlank()) {
            CuponEntity cupon = cuponService.findByCodigo(req.getCodigoCupon());
            descuento = subtotal.multiply(cupon.getPorcentaje())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            cuponAplicado = cupon.getCodigo();
        }

        BigDecimal total = subtotal.subtract(descuento);

        // Crear pedido
        PedidoEntity pedido = PedidoEntity.builder()
            .usuario(usuario)
            .subtotal(subtotal)
            .descuento(descuento)
            .total(total)
            .cuponAplicado(cuponAplicado)
            .direccionEntrega(req.getDireccionEntrega())
            .fechaEntregaDeseada(req.getFechaEntregaDeseada() != null
                ? LocalDate.parse(req.getFechaEntregaDeseada()) : null)
            .build();

        // Crear detalles y descontar stock
        List<DetallePedidoEntity> detalles = items.stream().map(item -> {
            // Descontar stock
            item.getProducto().setStock(item.getProducto().getStock() - item.getCantidad());

            return DetallePedidoEntity.builder()
                .pedido(pedido)
                .producto(item.getProducto())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .build();
        }).toList();

        pedido.setDetalles(detalles);
        PedidoEntity saved = pedidoRepo.save(pedido);

        // Vaciar carrito
        carritoService.vaciar(correo);

        return toResponse(saved);
    }

    @Override
    public List<PedidoResponse> historial(String correo) {
        UsuarioEntity usuario = getUsuario(correo);
        return pedidoRepo.findByUsuarioIdOrderByCreadoEnDesc(usuario.getId())
            .stream().map(this::toResponse).toList();
    }

    @Override
    public PedidoResponse findById(Long id, String correo) {
        PedidoEntity pedido = pedidoRepo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));
        // Verificar que el pedido pertenece al usuario (a menos que sea admin/vendedor)
        if (!pedido.getUsuario().getCorreo().equals(correo))
            throw new ReglaDeNegocioException("No tienes acceso a este pedido");
        return toResponse(pedido);
    }

    @Override
    public List<PedidoResponse> findAll() {
        return pedidoRepo.findAllByOrderByCreadoEnDesc().stream().map(this::toResponse).toList();
    }

    @Override
    public List<PedidoResponse> findByEstado(String estado) {
        try {
            EstadoPedido ep = EstadoPedido.valueOf(estado.toUpperCase());
            return pedidoRepo.findByEstadoOrderByCreadoEnDesc(ep).stream().map(this::toResponse).toList();
        } catch (IllegalArgumentException e) {
            throw new ReglaDeNegocioException("Estado inválido: " + estado +
                ". Valores: PENDIENTE, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO");
        }
    }

    @Override
    @Transactional
    public PedidoResponse cambiarEstado(Long id, String nuevoEstado) {
        PedidoEntity pedido = pedidoRepo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));
        try {
            pedido.setEstado(EstadoPedido.valueOf(nuevoEstado.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ReglaDeNegocioException("Estado inválido: " + nuevoEstado);
        }
        return toResponse(pedidoRepo.save(pedido));
    }

    // ── helpers ──────────────────────────────────────────────────────

    private UsuarioEntity getUsuario(String correo) {
        return usuarioRepo.findByCorreo(correo)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    private PedidoResponse toResponse(PedidoEntity p) {
        List<PedidoResponse.DetalleResponse> detalles = p.getDetalles().stream()
            .map(d -> PedidoResponse.DetalleResponse.builder()
                .productoId(d.getProducto().getId())
                .productoNombre(d.getProducto().getNombre())
                .productoImagenUrl(d.getProducto().getImagenUrl())
                .cantidad(d.getCantidad())
                .precioUnitario(d.getPrecioUnitario())
                .subtotal(d.getSubtotal())
                .build())
            .toList();

        return PedidoResponse.builder()
            .id(p.getId())
            .estado(p.getEstado().name())
            .direccionEntrega(p.getDireccionEntrega())
            .fechaEntregaDeseada(p.getFechaEntregaDeseada())
            .cuponAplicado(p.getCuponAplicado())
            .subtotal(p.getSubtotal())
            .descuento(p.getDescuento())
            .total(p.getTotal())
            .creadoEn(p.getCreadoEn())
            .detalles(detalles)
            .build();
    }
}
