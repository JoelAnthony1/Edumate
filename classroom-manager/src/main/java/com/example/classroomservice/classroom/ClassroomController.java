package com.example.classroomservice.classroom;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class ClassroomController {
    private ClassroomRepository classroomRepository;

    public ClassroomController(ClassroomRepository classroomRepository){
        this.classroomRepository = classroomRepository;
    }

    @ResponseBody
    @GetMapping("/classrooms")
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();  // This will return the list of admins in JSON format
    }
}
