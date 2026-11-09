package com.huerto.hogar.reporte.controller;

import com.huerto.hogar.pedido.entity.EstadoPedido;
import com.huerto.hogar.pedido.repository.DetallePedidoRepository;
import com.huerto.hogar.pedido.repository.PedidoRepository;
import com.huerto.hogar.producto.dto.ProductoResponse;
import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.producto.repository.ProductoRepository;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import com.huerto.hogar.reporte.dto.ProductoVendidoDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5273"})
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

    @Operation(summary = "Resumen general",
            description = "Totales de usuarios, productos activos y pedidos, más cantidad de pedidos por estado. Solo administrador o vendedor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumen general"),
        @ApiResponse(responseCode = "400", description = "Error al generar el reporte"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR o VENDEDOR")
    })
    @SecurityRequirement(name = "bearerAuth")
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

    @Operation(summary = "Reporte de stock crítico",
            description = "Productos con stock en o bajo el nivel crítico. Solo administrador o vendedor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Productos con stock crítico",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Error al generar el reporte"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR o VENDEDOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/stock-critico")
    public ResponseEntity<?> stockCritico() {
        try {
            return ResponseEntity.ok(productoRepo.findStockCriticoConCategoria()
                .stream().map(this::toResponse).toList());
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Pedidos por estado",
            description = "Cantidad de pedidos por cada estado (0 si no hay). Solo administrador o vendedor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conteo de pedidos por estado"),
        @ApiResponse(responseCode = "400", description = "Error al generar el reporte"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR o VENDEDOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/pedidos-por-estado")
    public ResponseEntity<?> pedidosPorEstado() {
        try { return ResponseEntity.ok(contarPorEstado()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Productos más vendidos",
            description = "Los 5 productos con más unidades vendidas, excluyendo pedidos cancelados. Solo administrador o vendedor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ranking de productos más vendidos",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoVendidoDto.class)))),
        @ApiResponse(responseCode = "400", description = "Error al generar el reporte"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR o VENDEDOR")
    })
    @SecurityRequirement(name = "bearerAuth")
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
