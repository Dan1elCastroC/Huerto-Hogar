package com.huerto.hogar.frontend.model;

import java.math.BigDecimal;

public class Producto {
    public Long id;
    public String codigo;
    public String nombre;
    public String descripcion;
    public BigDecimal precio;
    public Integer stock;
    public Integer stockCritico;
    public boolean stockCriticoAlerta;
    public String imagenUrl;
    public Long categoriaId;
    public String categoriaNombre;
    public boolean activo;
}
