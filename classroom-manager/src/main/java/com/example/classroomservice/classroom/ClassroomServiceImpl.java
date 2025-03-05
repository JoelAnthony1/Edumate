package com.example.classroomservice.classroom;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;

    public ClassroomServiceImpl(ClassroomRepository classroomRepository){
        this.classroomRepository=classroomRepository;
    }

    @Override
    public List<Classroom> listClassrooms(){
        return classroomRepository.findAll();
    }
    @Override
    public Classroom getClassroom(Long id) {
        return classroomRepository.findById(id).orElse(null); 
    }

    @Override
    public Classroom addClassroom(Classroom classroom){
        return classroomRepository.save(classroom);
    }

    @Override
    public Classroom updateClassroom(Long id, Classroom newClassroomInfo) {
        return classroomRepository.findById(id).map(classroom -> {
            classroom.setClassname(newClassroomInfo.getClassname());
            classroom.setCapacity(newClassroomInfo.getCapacity());
            return classroomRepository.save(classroom);
        }).orElse(null);
    }
    @Override
    public void deleteClassroom(Long id) {
        classroomRepository.deleteById(id);
    }
}
