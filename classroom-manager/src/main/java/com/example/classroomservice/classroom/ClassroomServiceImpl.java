package com.example.classroomservice.classroom;
import java.util.*;

import org.springframework.stereotype.Service;
import com.example.classroomservice.student.*;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;

    public ClassroomServiceImpl(ClassroomRepository classroomRepository, StudentRepository studentRepository){
        this.classroomRepository=classroomRepository;
        this.studentRepository=studentRepository;
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

    public void assignStudentToClassroom(Long studentId, Long classroomId) {
        Optional<Student> student = studentRepository.findById(studentId);
        Optional<Classroom> classroom = classroomRepository.findById(classroomId);

        if (student.isPresent() && classroom.isPresent()) {
            classroom.get().getStudents().add(student.get());
            classroomRepository.save(classroom.get());
        } else {
            throw new IllegalArgumentException("Student or Classroom not found");
        }
    }

}
