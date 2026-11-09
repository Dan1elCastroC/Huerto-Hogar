package com.huerto.hogar;

import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.exception.GlobalExceptionHandler;
import com.huerto.hogar.pedido.entity.EstadoPedido;
import com.huerto.hogar.pedido.repository.DetallePedidoRepository;
import com.huerto.hogar.pedido.repository.PedidoRepository;
import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.producto.repository.ProductoRepository;
import com.huerto.hogar.region.RegionesController;
import com.huerto.hogar.reporte.controller.ReporteController;
import com.huerto.hogar.reporte.dto.ProductoVendidoDto;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReporteYRegionControllerTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    class Reportes {
        static final String BASE = "/api/v1/entities/reportes";
        @Mock UsuarioRepository usuarioRepo;
        @Mock ProductoRepository productoRepo;
        @Mock PedidoRepository pedidoRepo;
        @Mock DetallePedidoRepository detalleRepo;
        @InjectMocks ReporteController controller;
        MockMvc mvc;

        @BeforeEach void preparar() {
            mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        }

        @Test void resumenIncluyeTotalesYTodosLosEstados() throws Exception {
            when(usuarioRepo.count()).thenReturn(4L);
            when(productoRepo.countByActivoTrue()).thenReturn(10L);
            when(pedidoRepo.count()).thenReturn(3L);
            when(pedidoRepo.countByEstado(EstadoPedido.PENDIENTE)).thenReturn(2L);
            when(pedidoRepo.countByEstado(EstadoPedido.EN_PREPARACION)).thenReturn(0L);
            when(pedidoRepo.countByEstado(EstadoPedido.ENVIADO)).thenReturn(0L);
            when(pedidoRepo.countByEstado(EstadoPedido.ENTREGADO)).thenReturn(1L);
            when(pedidoRepo.countByEstado(EstadoPedido.CANCELADO)).thenReturn(0L);
            mvc.perform(get(BASE + "/resumen")).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").value(4))
                .andExpect(jsonPath("$.totalProductos").value(10))
                .andExpect(jsonPath("$.totalPedidos").value(3))
                .andExpect(jsonPath("$.pedidosPorEstado.PENDIENTE").value(2))
                .andExpect(jsonPath("$.pedidosPorEstado.CANCELADO").value(0));
        }

        @Test void resumenConErrorRetorna400() throws Exception {
            when(usuarioRepo.count()).thenThrow(new RuntimeException("bd caída"));
            mvc.perform(get(BASE + "/resumen")).andExpect(status().isBadRequest())
                .andExpect(content().string("bd caída"));
        }

        @Test void stockCriticoMapeaProductosConAlerta() throws Exception {
            CategoriaEntity cat = CategoriaEntity.builder().id(2L).nombre("Frutas").build();
            ProductoEntity p = ProductoEntity.builder().id(1L).codigo("FR001").nombre("Manzana")
                .precio(BigDecimal.TEN).stock(2).stockCritico(5).categoria(cat).build();
            when(productoRepo.findStockCriticoConCategoria()).thenReturn(List.of(p));
            mvc.perform(get(BASE + "/stock-critico")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("FR001"))
                .andExpect(jsonPath("$[0].stockCriticoAlerta").value(true))
                .andExpect(jsonPath("$[0].categoriaNombre").value("Frutas"));
        }

        @Test void stockCriticoConErrorRetorna400() throws Exception {
            when(productoRepo.findStockCriticoConCategoria()).thenThrow(new RuntimeException("mal"));
            mvc.perform(get(BASE + "/stock-critico")).andExpect(status().isBadRequest());
        }

        @Test void pedidosPorEstadoDevuelveConteos() throws Exception {
            when(pedidoRepo.countByEstado(any(EstadoPedido.class))).thenReturn(1L);
            mvc.perform(get(BASE + "/pedidos-por-estado")).andExpect(status().isOk())
                .andExpect(jsonPath("$.ENVIADO").value(1));
        }

        @Test void pedidosPorEstadoConErrorRetorna400() throws Exception {
            when(pedidoRepo.countByEstado(any(EstadoPedido.class))).thenThrow(new RuntimeException("mal"));
            mvc.perform(get(BASE + "/pedidos-por-estado")).andExpect(status().isBadRequest());
        }

        @Test void productosMasVendidosExcluyeCancelados() throws Exception {
            when(detalleRepo.findMasVendidos(eq_(EstadoPedido.CANCELADO), any(Pageable.class)))
                .thenReturn(List.of(new ProductoVendidoDto(1L, "Manzana", 30L)));
            mvc.perform(get(BASE + "/productos-mas-vendidos")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].unidadesVendidas").value(30));
        }

        @Test void productosMasVendidosConErrorRetorna400() throws Exception {
            when(detalleRepo.findMasVendidos(any(), any())).thenThrow(new RuntimeException("mal"));
            mvc.perform(get(BASE + "/productos-mas-vendidos")).andExpect(status().isBadRequest());
        }

        private EstadoPedido eq_(EstadoPedido e) { return org.mockito.ArgumentMatchers.eq(e); }
    }

    @Nested
    class Regiones {
        final MockMvc mvc = MockMvcBuilders.standaloneSetup(new RegionesController()).build();

        @Test void listarRegionesDevuelveLas16() throws Exception {
            mvc.perform(get("/api/regiones")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(16))
                .andExpect(jsonPath("$[0]").value("Arica y Parinacota"));
        }

        @Test void comunasDeUnaRegionIgnoraMayusculas() throws Exception {
            mvc.perform(get("/api/regiones/maule/comunas")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Talca"));
        }

        @Test void comunasDeRegionInexistenteDevuelveListaVacia() throws Exception {
            mvc.perform(get("/api/regiones/Narnia/comunas")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test void todasDevuelveMapaRegionComunas() throws Exception {
            mvc.perform(get("/api/regiones/todas")).andExpect(status().isOk())
                .andExpect(jsonPath("$.Maule").isArray())
                .andExpect(jsonPath("$['Metropolitana de Santiago']").isArray());
        }
    }
}
