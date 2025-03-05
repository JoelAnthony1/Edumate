package com.example.classroomservice.classroom;

import java.util.List;

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


@Controller
public class ClassroomController {
    private ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService){
        this.classroomService = classroomService;
    }

    @ResponseBody
    @GetMapping("/classrooms")
    public List<Classroom> getAllClassrooms() {
        return classroomService.listClassrooms(); 
    }

    @ResponseBody
    @GetMapping("/classrooms/{id}")
    public Classroom getClassroom(@PathVariable Long id) {
        Classroom classroom = classroomService.getClassroom(id);
        if (classroom == null)
            throw new ClassroomNotFoundException(id);
        return classroomService.getClassroom(id);
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
}
