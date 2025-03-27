package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Analysis;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisRepo extends JpaRepository<Analysis, Long>{

}
