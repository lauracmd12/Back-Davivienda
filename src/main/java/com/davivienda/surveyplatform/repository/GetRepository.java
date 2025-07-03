// UserRepository
package com.davivienda.surveyplatform.repository;

import com.davivienda.surveyplatform.entity.LoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GetRepository extends JpaRepository<LoginEntity, Long> {

    Optional<LoginEntity> findByEmailAndPasswordHash(String email, String passwordHash);
    Optional<LoginEntity> findByEmail(String email);
}