package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.gakuseiadmin.content.AdminLessonRepository;

@RestController
public class AdminLessonController {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    AdminLessonRepository adminLessonRepository;

    @RequestMapping(
            value = "/api/lessons/page/{offset}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Page<Lesson>> getLessons(@PathVariable(value = "offset") int offset,
                                                   @RequestParam(value = "pageSize") int pageSize) {
        return new ResponseEntity<>(adminLessonRepository.findAll(new PageRequest(offset, pageSize)), HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/lessons/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Lesson> getLesson(@PathVariable(value = "id") Long id) {
        Lesson lesson = lessonRepository.findOne(id);
        if (lesson != null) {
            return new ResponseEntity<>(lesson, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
            value = "/api/lessons",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Lesson> updateLesson(@RequestBody Lesson lesson){
        if(lessonRepository.exists(lesson.getId())){
            return new ResponseEntity<>(lessonRepository.save(lesson), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
