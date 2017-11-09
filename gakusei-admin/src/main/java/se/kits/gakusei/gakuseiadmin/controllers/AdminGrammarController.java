package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.content.model.Inflection;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.repository.InflectionRepository;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.content.repository.NuggetRepository;
import se.kits.gakusei.gakuseiadmin.util.AdminGrammarHandler;
import se.sandboge.japanese.conjugation.Verb;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
public class AdminGrammarController {

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    InflectionRepository inflectionRepository;

    @Autowired
    NuggetRepository nuggetRepository;

    @Autowired
    AdminGrammarHandler adminGrammarHandler;

    @RequestMapping(
            value = "/api/grammar",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<HashMap<String, Object>>> getGrammarLists(){

        List<HashMap<String, Object>> grammarLists = new ArrayList<>();

        for(Lesson lesson : lessonRepository.findVocabularyLessons()){
            HashMap<String, Object> grammarList = new HashMap<>();
            HashMap<String, List<Inflection>> inflections = new HashMap<>();

            List<Nugget> nuggets = nuggetRepository.getNuggetsWithWordType("verb", "english", "swedish");

            inflections.put("used", inflectionRepository.findByLesson_Id(lesson.getId()));
            inflections.put("unused", adminGrammarHandler.getUnusedInflectionMethods(lesson));

            grammarList.put("lesson", lesson);
            grammarList.put("inflections", inflections); // TODO : Add allowed methods of inflection
            grammarList.put("nuggets", nuggets);

            grammarLists.add(grammarList);
        }

        return new ResponseEntity<>(grammarLists, HttpStatus.OK);
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
