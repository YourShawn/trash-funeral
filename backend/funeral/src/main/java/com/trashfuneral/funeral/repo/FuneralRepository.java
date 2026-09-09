package com.trashfuneral.funeral.repo;

import com.trashfuneral.auth.domain.User;
import com.trashfuneral.funeral.domain.Funeral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuneralRepository extends JpaRepository<Funeral, Long> {

    List<Funeral> findByUserOrderByCreatedAtDesc(User user);

    Optional<Funeral> findByIdAndUser(Long id, User user);

    Optional<Funeral> findByPublicToken(String publicToken);
}
