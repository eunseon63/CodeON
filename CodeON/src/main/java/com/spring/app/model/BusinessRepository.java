package com.spring.app.model;

import org.springframework.data.jpa.repository.JpaRepository;
import com.spring.app.entity.Business;

public interface BusinessRepository extends JpaRepository<Business, Long> {}
