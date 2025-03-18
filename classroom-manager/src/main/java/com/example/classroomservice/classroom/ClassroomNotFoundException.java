package com.example.classroomservice.classroom;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClassroomNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClassroomNotFoundException(Long id) {
        super("Could not find classroom " + id);
    }      
}
