package com.huerto.hogar.carrito.interfaces;

import com.huerto.hogar.carrito.dto.CarritoResponse;
import com.huerto.hogar.carrito.dto.CarritoItemDto;

public interface ICarritoService {
    CarritoResponse getCarrito(String correo, String codigoCupon);
    CarritoResponse agregar(String correo, CarritoItemDto dto);
    CarritoResponse actualizar(String correo, Long productoId, Integer cantidad);
    CarritoResponse eliminarItem(String correo, Long productoId);
    void vaciar(String correo);
}
