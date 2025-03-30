package com.example.classroomservice.classroom;

import java.util.*;
import com.example.classroomservice.student.Student;


public interface ClassroomService {
    Set<Student> getListOfStudents(Long id);
    List<Classroom> findByUserId(Long userId);
    Classroom getClassroom(Long id);
    Classroom addClassroom(Classroom classroom);
    Classroom updateClassroom(Long id, Classroom classroom);
    void deleteClassroom(Long id);
    void assignStudentToClassroom(Long studentId, Long classroomId);
    void removeStudentFromClassroom(Long studentId, Long classroomId);
}
