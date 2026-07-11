package com.divya.minicrm.repository;

import com.divya.minicrm.entity.Lead;
import com.divya.minicrm.enums.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByStatus(LeadStatus status);

    List<Lead> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
}
