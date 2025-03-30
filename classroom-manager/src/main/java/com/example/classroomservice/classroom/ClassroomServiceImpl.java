package com.example.classroomservice.classroom;
import java.util.*;

import org.springframework.stereotype.Service;
import com.example.classroomservice.student.*;
import com.example.classroomservice.exception.*;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;

    public ClassroomServiceImpl(ClassroomRepository classroomRepository, StudentRepository studentRepository){
        this.classroomRepository=classroomRepository;
        this.studentRepository=studentRepository;
    }

    @Override
    public List<Classroom> findByUserId(Long userId){
        return classroomRepository.findByUserId(userId);
    }
    @Override
    public Classroom getClassroom(Long id) {
        return classroomRepository.findById(id).orElse(null); 
    }
    @Override
    public Set<Student> getListOfStudents(Long id){
        return classroomRepository.findStudentsByClassroomId(id);
    }
    @Override
    public Classroom addClassroom(Classroom classroom){
        return classroomRepository.save(classroom);
    }

    @Override
    public Classroom updateClassroom(Long id, Classroom newClassroomInfo) {
        return classroomRepository.findById(id).map(classroom -> {
            classroom.setClassname(newClassroomInfo.getClassname());
            classroom.setSubject(newClassroomInfo.getSubject());
            classroom.setDescription(newClassroomInfo.getDescription());
            return classroomRepository.save(classroom);
        }).orElse(null);
    }
    @Override
    public void deleteClassroom(Long id) {
        classroomRepository.deleteById(id);
    }
    @Override
    public Student getStudentFromClass(Long studentId, Long classroomId ) {
        Optional<Classroom> classroomOpt = classroomRepository.findById(classroomId);

        if(classroomOpt.isPresent()){
            Classroom classroom = classroomOpt.get();
            for (Student student : classroom.getStudents()){
                if (student.getId().equals(studentId)){
                    return student;
                }
            }
            throw new IllegalArgumentException("Student not found in classroom");
        }else {
            throw new IllegalArgumentException("Classroom not found");
        }
    }

    @Override
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

    @Override
    public void removeStudentFromClassroom(Long studentId, Long classroomId) {
        Optional<Classroom> classroom = classroomRepository.findById(classroomId);
        if (classroom.isPresent()) {
            Classroom foundClassroom = classroom.get();
            Optional<Student> student = studentRepository.findById(studentId);
            
            if (student.isPresent()) {
                if (foundClassroom.getStudents().remove(student.get())) {
                    classroomRepository.save(foundClassroom);
                } else {
                    throw new IllegalArgumentException("Student with ID " + studentId + " is not in the classroom");
                }
            } else {
                throw new IllegalArgumentException("Student not found");
            }
        } else {
            throw new IllegalArgumentException("Classroom not found");
        }
    }
    
    

}
