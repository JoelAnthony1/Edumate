package com.example.classroomservice.classroom;

import java.util.*;
// import javax.validation.constraints.Min;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.classroomservice.student.Student;
import org.springframework.http.ResponseEntity;

@RestController
public class ClassroomController {
    private ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService){
        this.classroomService = classroomService;
    }

    @ResponseBody
    @GetMapping("/classrooms")
public List<Classroom> getAllClassrooms(@RequestParam(required = false) Long userId) {
    if (userId == null) {
        throw new IllegalArgumentException("User ID is required");
    }
    return classroomService.findByUserId(userId);
}

    @ResponseBody
    @GetMapping("/classrooms/{id}")
    public Classroom getClassroom(@PathVariable Long id) {
        Classroom classroom = classroomService.getClassroom(id);
        if (classroom == null)
            throw new ClassroomNotFoundException(id);
        return classroomService.getClassroom(id);
    }

    @GetMapping("/classrooms/{id}/students")
    public Set<Student> getListOfStudents(@PathVariable Long id){
        Set<Student> students = classroomService.getListOfStudents(id);
        if (students == null){
            throw new IllegalArgumentException("no students");
        }
        return students;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @PostMapping("/classrooms")
    public Classroom addClassroom(@RequestBody Classroom classroom) {
        return classroomService.addClassroom(classroom);
    }

    @DeleteMapping("/classrooms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClassroom(@PathVariable Long id) {
        if (classroomService.getClassroom(id) == null){
            throw new ClassroomNotFoundException(id);
        }
        classroomService.deleteClassroom(id);
    }

    @ResponseBody
    @PutMapping("/classrooms/{id}")
    public Classroom updateClassroom(@PathVariable Long id, @RequestBody Classroom newClassroomInfo) {
        Classroom classroom = classroomService.updateClassroom(id, newClassroomInfo);
        if (classroom == null){
            throw new ClassroomNotFoundException(id);
        }
        return classroom;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @PostMapping("/classrooms/{classroomId}/students/{studentId}")
    public void assignStudent(@PathVariable Long classroomId, @PathVariable Long studentId) {
        classroomService.assignStudentToClassroom(studentId, classroomId);
    }

    @DeleteMapping("/classrooms/{classroomId}/students/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStudentFromClassroom(@PathVariable Long classroomId, @PathVariable Long studentId) {
        classroomService.removeStudentFromClassroom(studentId,classroomId);
    }

    @ResponseBody
    @GetMapping("/classrooms/{classroomId}/students/{studentId}")
    public Student getStudentFromClass(@PathVariable Long classroomId, @PathVariable Long studentId) {
        return classroomService.getStudentFromClass(studentId, classroomId);
    }
}
