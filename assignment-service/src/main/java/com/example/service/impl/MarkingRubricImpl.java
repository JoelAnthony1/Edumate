package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.MarkingRubric;
import com.example.repository.MarkingRubricRepo;
import com.example.service.MarkingRubricService;

@Service
public class MarkingRubricImpl implements MarkingRubricService{

    private final MarkingRubricRepo markingrubricRepo;

    @Autowired
    public MarkingRubricImpl(MarkingRubricRepo markingRubricRepo) {
        this.markingrubricRepo = markingRubricRepo;
    }

    @Override
    public MarkingRubric createMarkingRubric(MarkingRubric markingRubric){
        return markingrubricRepo.save(markingRubric);
    }

}
