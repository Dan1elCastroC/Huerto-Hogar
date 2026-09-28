package com.huerto.hogar.producto.interfaces;
import com.huerto.hogar.producto.dto.ProductoDto;
import com.huerto.hogar.producto.dto.ProductoResponse;
import java.util.List;
public interface IProductoService {
    List<ProductoResponse> findAll();
    ProductoResponse findById(Long id);
    List<ProductoResponse> buscar(String query);
    List<ProductoResponse> findByCategoria(Long categoriaId);
    List<ProductoResponse> findStockCritico();
    ProductoResponse save(ProductoDto dto);
    ProductoResponse update(Long id, ProductoDto dto);
    void deleteById(Long id);
}
