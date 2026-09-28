package com.huerto.hogar.pedido.interfaces;

import com.huerto.hogar.pedido.dto.CheckoutRequest;
import com.huerto.hogar.pedido.dto.PedidoResponse;
import java.util.List;

public interface IPedidoService {
    // Cliente
    PedidoResponse checkout(String correo, CheckoutRequest req);
    List<PedidoResponse> historial(String correo);
    PedidoResponse findById(Long id, String correo);
    // Admin / Vendedor
    List<PedidoResponse> findAll();
    List<PedidoResponse> findByEstado(String estado);
    PedidoResponse cambiarEstado(Long id, String nuevoEstado);
}
