package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.MarkingRubric;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkingRubricRepo extends JpaRepository<MarkingRubric, Long>{

}
