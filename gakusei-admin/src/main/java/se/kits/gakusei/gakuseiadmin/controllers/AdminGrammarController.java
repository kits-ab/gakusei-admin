package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.repository.LessonRepository;

import java.util.*;
import java.util.HashMap;

@RestController
public class AdminGrammarController {

    @Autowired
    LessonRepository lessonRepository;

    @RequestMapping(
            value = "/api/grammar",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<HashMap<String, Object>>> getGrammarLists(){

        List<HashMap<String, Object>> grammarLists = new ArrayList<>();

        for(Lesson lesson : lessonRepository.findVocabularyLessons()){
            HashMap<String, Object> grammarList = new HashMap<>();
            grammarList.put("lesson", lesson.getName());
            grammarList.put("inflections", Collections.EMPTY_LIST); // TODO : Add allowed methods of inflection
            grammarList.put("nuggets", lessonRepository.findKanjiLessNuggetsByFactType(lesson.getName(), "reading", "swedish"));

            grammarLists.add(grammarList);
        }

        return new ResponseEntity<>(grammarLists, HttpStatus.OK);
    }
}
