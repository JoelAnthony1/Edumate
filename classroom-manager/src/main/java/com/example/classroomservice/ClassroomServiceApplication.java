package com.example.classroomservice;

import com.example.classroomservice.classroom.Classroom;
import com.example.classroomservice.classroom.ClassroomRepository;
import com.example.classroomservice.student.Student;
import com.example.classroomservice.student.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class ClassroomServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassroomServiceApplication.class, args);
    }

    // @Bean
    // @Transactional
    // CommandLineRunner initDatabase(ClassroomRepository classroomRepository,
    //                              StudentRepository studentRepository) {
    //     return args -> {
    //         // Clear existing data
    //         classroomRepository.deleteAll();
    //         studentRepository.deleteAll();

    //         // Create and save students first
    //         Student marcus = new Student();
    //         marcus.setName("Marcus");
    //         marcus = studentRepository.save(marcus); // Save and get managed entity

    //         Student joel = new Student();
    //         joel.setName("Joel");
    //         joel = studentRepository.save(joel);

    //         Student jeffrey = new Student();
    //         jeffrey.setName("Jeffrey");
    //         jeffrey = studentRepository.save(jeffrey);

    //         Student solomon = new Student();
    //         solomon.setName("Solomon");
    //         solomon = studentRepository.save(solomon);

    //         Student yangHwee = new Student();
    //         yangHwee.setName("Yang Hwee");
    //         yangHwee = studentRepository.save(yangHwee);

    //         Student lennel = new Student();
    //         lennel.setName("Lennel");
    //         lennel = studentRepository.save(lennel);

    //         // Create and save classrooms
    //         Classroom classroom1 = new Classroom();
    //         classroom1.setUserId(1L);
    //         classroom1.setClassname("Mathematics 101");
    //         classroom1.setSubject("Math");
    //         classroom1.setDescription("Basic Algebra");
    //         classroom1.addStudent(marcus);
    //         classroom1.addStudent(joel);
    //         classroomRepository.save(classroom1);

    //         Classroom classroom2 = new Classroom();
    //         classroom2.setUserId(1L);
    //         classroom2.setClassname("Science 201");
    //         classroom2.setSubject("Physics");
    //         classroom2.setDescription("Newtonian Mechanics");
    //         classroom2.addStudent(jeffrey);
    //         classroom2.addStudent(solomon);
    //         classroom2.addStudent(yangHwee);
    //         classroomRepository.save(classroom2);

    //         Classroom classroom3 = new Classroom();
    //         classroom3.setUserId(1L);
    //         classroom3.setClassname("Programming 301");
    //         classroom3.setSubject("Computer Science");
    //         classroom3.setDescription("Advanced Java Programming");
    //         classroom3.addStudent(lennel);
    //         classroom3.addStudent(marcus); // Marcus is in two classes
    //         classroomRepository.save(classroom3);

    //         System.out.println("Test data initialized successfully!");
    //     };
    // }
}