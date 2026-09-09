package com.trashfuneral.funeral.repo;

import com.trashfuneral.funeral.domain.ObjectType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ObjectTypeRepository extends JpaRepository<ObjectType, Long> {

    Optional<ObjectType> findByCode(String code);
}
