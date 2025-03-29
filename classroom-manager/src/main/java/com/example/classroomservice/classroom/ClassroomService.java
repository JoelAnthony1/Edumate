package com.example.classroomservice.classroom;

import com.example.classroomservice.student.Student;
import java.util.*;

public interface ClassroomService {
    // List<Classroom> listClassrooms();
    Set<Student> getListOfStudents(Long id);
    List<Classroom> findByUserId(Long userId);
    Classroom getClassroom(Long id);
    Classroom addClassroom(Classroom classroom);
    Classroom updateClassroom(Long id, Classroom classroom);
    void deleteClassroom(Long id);
    void assignStudentToClassroom(Long studentId, Long classroomId);
    void removeStudentFromClassroom(Long studentId, Long classroomId);
}
