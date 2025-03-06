package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.GradingResult;
import org.springframework.stereotype.Repository;

@Repository
public interface GradingResultRepo extends JpaRepository<GradingResult, Long>{

}
