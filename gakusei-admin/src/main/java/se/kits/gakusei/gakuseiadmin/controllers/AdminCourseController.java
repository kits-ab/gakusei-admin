package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import se.kits.gakusei.content.model.Course;
import se.kits.gakusei.gakuseiadmin.content.AdminCourseRepository;

@RestController
public class AdminCourseController {

    @Autowired
    AdminCourseRepository adminCourseRepository;

    public ResponseEntity<Course> createCourse(){
        return null;
    }

    public ResponseEntity<Course> updateCourse(){
        return null;
    }

    public ResponseEntity<Course> deleteCourse(){
        return null;
    }
}
