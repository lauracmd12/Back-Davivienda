package com.davivienda.surveyplatform.repository;

import com.davivienda.surveyplatform.entity.LoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsertRepository extends JpaRepository<LoginEntity, Long> {


}
