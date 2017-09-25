package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.content.model.Course;
import se.kits.gakusei.content.repository.CourseRepository;

@RestController
public class AdminCourseController {

    @Autowired
    CourseRepository courseRepository;

    @RequestMapping(
            value = "api/courses",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Course> createCourse(@RequestBody Course course){
        if(courseRepository.exists(course.getId())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(courseRepository.save(course), HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "api/courses",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Course> updateCourse(@RequestBody Course course){
        if(courseRepository.exists(course.getId())){
            return new ResponseEntity<>(courseRepository.save(course), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "api/courses/{courseID}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Course> deleteCourse(@PathVariable(value = "courseID") Long courseID){
        if(courseRepository.exists(courseID)){
            courseRepository.delete(courseID);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
