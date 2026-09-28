package com.huerto.hogar.blog.repository;

import com.huerto.hogar.blog.entity.BlogEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BlogRepository extends CrudRepository<BlogEntity, Long> {
    List<BlogEntity> findByActivoTrueOrderByCreadoEnDesc();
}
