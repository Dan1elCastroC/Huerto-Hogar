package com.huerto.hogar.reporte.controller;

import com.huerto.hogar.pedido.entity.EstadoPedido;
import com.huerto.hogar.pedido.repository.DetallePedidoRepository;
import com.huerto.hogar.pedido.repository.PedidoRepository;
import com.huerto.hogar.producto.dto.ProductoResponse;
import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.producto.repository.ProductoRepository;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5273")
@RestController
@RequestMapping("/api/v1/entities/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes")
@PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
public class ReporteController {

    private static final int TOP_VENDIDOS = 5;

    private final UsuarioRepository usuarioRepo;
    private final ProductoRepository productoRepo;
    private final PedidoRepository pedidoRepo;
    private final DetallePedidoRepository detalleRepo;

    @GetMapping("/resumen")
    public ResponseEntity<?> resumen() {
        try {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("totalUsuarios", usuarioRepo.count());
            r.put("totalProductos", productoRepo.countByActivoTrue());
            r.put("totalPedidos", pedidoRepo.count());
            r.put("pedidosPorEstado", contarPorEstado());
            return ResponseEntity.ok(r);
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @GetMapping("/stock-critico")
    public ResponseEntity<?> stockCritico() {
        try {
            return ResponseEntity.ok(productoRepo.findStockCriticoConCategoria()
                .stream().map(this::toResponse).toList());
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @GetMapping("/pedidos-por-estado")
    public ResponseEntity<?> pedidosPorEstado() {
        try { return ResponseEntity.ok(contarPorEstado()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<?> productosMasVendidos() {
        try {
            return ResponseEntity.ok(detalleRepo.findMasVendidos(
                EstadoPedido.CANCELADO, PageRequest.of(0, TOP_VENDIDOS)));
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    // Incluye todos los estados, con 0 cuando no hay pedidos
    private Map<String, Long> contarPorEstado() {
        Map<String, Long> m = new LinkedHashMap<>();
        for (EstadoPedido e : EstadoPedido.values())
            m.put(e.name(), pedidoRepo.countByEstado(e));
        return m;
    }

    private ProductoResponse toResponse(ProductoEntity p) {
        return ProductoResponse.builder().id(p.getId()).codigo(p.getCodigo()).nombre(p.getNombre())
            .descripcion(p.getDescripcion()).precio(p.getPrecio()).stock(p.getStock())
            .stockCritico(p.getStockCritico()).stockCriticoAlerta(p.tieneStockCritico())
            .imagenUrl(p.getImagenUrl()).categoriaId(p.getCategoria().getId())
            .categoriaNombre(p.getCategoria().getNombre()).activo(p.isActivo()).build();
    }
}
