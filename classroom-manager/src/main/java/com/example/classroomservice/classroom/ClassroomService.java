package com.example.classroomservice.classroom;

import java.util.List;

public interface ClassroomService {
    List<Classroom> listClassrooms();
    Classroom getClassroom(Long id);
    Classroom addClassroom(Classroom classroom);
    Classroom updateClassroom(Long id, Classroom classroom);
    void deleteClassroom(Long id);
    void assignStudentToClassroom(Long studentId, Long classroomId);
}
