package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.repository.LessonRepository;

@RestController
public class AdminLessonController {

    @Autowired
    private LessonRepository lessonRepository;

    @RequestMapping(
            value = "/api/lessons/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @Cacheable("allLessons")
    public ResponseEntity<Iterable<Lesson>> getLessons() {

        return new ResponseEntity<>(lessonRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/lessons",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<?> createLesson(@RequestBody Lesson lesson) {
        if (lessonRepository.findByName(lesson.getName()) != null) {
            return new ResponseEntity<String>("Lesson name is not unique", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Lesson>(lessonRepository.save(lesson), HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "/api/lessons/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<String> deleteLesson(@PathVariable(value = "id") Long id) {
        if (!lessonRepository.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        lessonRepository.delete(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
