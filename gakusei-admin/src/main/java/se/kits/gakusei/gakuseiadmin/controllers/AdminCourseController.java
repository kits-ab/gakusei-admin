package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<Course> createCourse(@RequestBody Course course){
        if(adminCourseRepository.exists(course.getId())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(adminCourseRepository.save(course), HttpStatus.OK);
    }

    @RequestMapping(
            value = "api/courses/update",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Course> updateCourse(@RequestBody Course course){
        return null;
    }

    @RequestMapping(
            value = "api/courses/{courseID}/delete",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Course> deleteCourse(@PathVariable(value = "courseID") Long courseID){
        return null;
    }
}
