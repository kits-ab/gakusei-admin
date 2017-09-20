package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.kits.gakusei.content.model.Course;
import se.kits.gakusei.gakuseiadmin.content.AdminCourseRepository;

@RestController
public class AdminCourseController {

    @Autowired
    AdminCourseRepository adminCourseRepository;

    @RequestMapping(
            value = "api/courses/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Course> createCourse(){
        return null;
    }

    @RequestMapping(
            value = "api/courses/update",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Course> updateCourse(){
        return null;
    }

    @RequestMapping(
            value = "api/courses/{courseID}/delete",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Course> deleteCourse(){
        return null;
    }
}
