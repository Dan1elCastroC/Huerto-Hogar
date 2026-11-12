package com.huerto.hogar.cupon.interfaces;

import com.huerto.hogar.cupon.dto.CuponDto;
import com.huerto.hogar.cupon.dto.CuponResponse;

import java.util.List;

public interface ICuponService {
    List<CuponResponse> findAll();
    CuponResponse findById(Long id);
    CuponResponse findByCodigo(String codigo);
    CuponResponse save(CuponDto dto);
    CuponResponse update(Long id, CuponDto dto);
    void deleteById(Long id);
}
