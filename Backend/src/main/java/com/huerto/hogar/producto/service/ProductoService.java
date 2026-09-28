package com.huerto.hogar.producto.service;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.repository.CategoriaRepository;
import com.huerto.hogar.exception.*;
import com.huerto.hogar.producto.dto.*;
import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.producto.interfaces.IProductoService;
import com.huerto.hogar.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductoService implements IProductoService {
    private final ProductoRepository productoRepo;
    private final CategoriaRepository categoriaRepo;
    @Override public List<ProductoResponse> findAll() {
        return productoRepo.findByActivoTrue().stream().map(this::toResponse).toList();
    }
    @Override public ProductoResponse findById(Long id) { return toResponse(getOrThrow(id)); }
    @Override public List<ProductoResponse> buscar(String q) {
        return productoRepo.buscar(q).stream().map(this::toResponse).toList();
    }
    @Override public List<ProductoResponse> findByCategoria(Long catId) {
        return productoRepo.findByCategoriaIdAndActivoTrue(catId).stream().map(this::toResponse).toList();
    }
    @Override public List<ProductoResponse> findStockCritico() {
        return productoRepo.findStockCritico().stream().map(this::toResponse).toList();
    }
    @Override public ProductoResponse save(ProductoDto dto) {
        if (productoRepo.existsByCodigo(dto.getCodigo()))
            throw new ReglaDeNegocioException("Ya existe un producto con código: " + dto.getCodigo());
        CategoriaEntity cat = categoriaRepo.findById(dto.getCategoriaId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));
        return toResponse(productoRepo.save(ProductoEntity.builder()
            .codigo(dto.getCodigo()).nombre(dto.getNombre()).descripcion(dto.getDescripcion())
            .precio(dto.getPrecio()).stock(dto.getStock()).stockCritico(dto.getStockCritico())
            .imagenUrl(dto.getImagenUrl()).categoria(cat).build()));
    }
    @Override public ProductoResponse update(Long id, ProductoDto dto) {
        ProductoEntity p = getOrThrow(id);
        if (!p.getCodigo().equals(dto.getCodigo()) && productoRepo.existsByCodigo(dto.getCodigo()))
            throw new ReglaDeNegocioException("Código ya en uso por otro producto");
        CategoriaEntity cat = categoriaRepo.findById(dto.getCategoriaId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));
        p.setCodigo(dto.getCodigo()); p.setNombre(dto.getNombre()); p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio()); p.setStock(dto.getStock()); p.setStockCritico(dto.getStockCritico());
        p.setImagenUrl(dto.getImagenUrl()); p.setCategoria(cat);
        return toResponse(productoRepo.save(p));
    }
    @Override public void deleteById(Long id) {
        ProductoEntity p = getOrThrow(id); p.setActivo(false); productoRepo.save(p);
    }
    private ProductoEntity getOrThrow(Long id) {
        return productoRepo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }
    private ProductoResponse toResponse(ProductoEntity p) {
        return ProductoResponse.builder().id(p.getId()).codigo(p.getCodigo()).nombre(p.getNombre())
            .descripcion(p.getDescripcion()).precio(p.getPrecio()).stock(p.getStock())
            .stockCritico(p.getStockCritico()).stockCriticoAlerta(p.tieneStockCritico())
            .imagenUrl(p.getImagenUrl()).categoriaId(p.getCategoria().getId())
            .categoriaNombre(p.getCategoria().getNombre()).activo(p.isActivo()).build();
    }
}
