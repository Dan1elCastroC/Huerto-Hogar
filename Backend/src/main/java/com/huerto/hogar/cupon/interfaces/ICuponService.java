package com.huerto.hogar.cupon.interfaces;

import com.huerto.hogar.cupon.entity.CuponEntity;
import java.util.List;

public interface ICuponService {
    List<CuponEntity> findAll();
    CuponEntity findById(Long id);
    CuponEntity findByCodigo(String codigo);
    CuponEntity save(CuponEntity cupon);
    CuponEntity update(Long id, CuponEntity cupon);
    void deleteById(Long id);
}
