package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.content.model.Inflection;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.repository.InflectionRepository;
import se.kits.gakusei.gakuseiadmin.util.AdminGrammarHandler;

import java.util.*;
import java.util.HashMap;

@RestController
public class AdminGrammarController {

    @Autowired
    InflectionRepository inflectionRepository;

    @Autowired
    AdminGrammarHandler adminGrammarHandler;

    @RequestMapping(
            value = "/api/grammar/{lessonId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<HashMap<String, Object>> getGrammarLists(@PathVariable(value = "lessonId") Long lessonId){
        return new ResponseEntity<>(adminGrammarHandler.getGrammarList(lessonId), HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/grammar/inflection",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Inflection> createInflections(@RequestBody Inflection inflection){
        if(inflectionRepository.findByLessonAndInflectionMethod(inflection.getLesson(), inflection.getInflectionMethod()) != null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(inflectionRepository.save(inflection), HttpStatus.CREATED);
        }
    }

    @RequestMapping(
            value = "/api/grammar/inflection",
            method = RequestMethod.DELETE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<String> deleteInflections(@RequestBody Inflection inflection){
        Inflection toDelete = inflectionRepository.findByLessonAndInflectionMethod(inflection.getLesson(), inflection.getInflectionMethod());
        if(toDelete != null) {
            inflectionRepository.delete(toDelete);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
