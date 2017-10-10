package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.gakuseiadmin.util.csv.CSVQuiz;
import se.kits.gakusei.gakuseiadmin.util.FormValidator;
import se.kits.gakusei.gakuseiadmin.util.AdminQuizHandler;
import se.kits.gakusei.gakuseiadmin.content.AdminQuizRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminQuizController {

    @Autowired
    AdminQuizRepository quizRepository;

    @Autowired
    AdminQuizHandler adminQuizHandler;

    @Autowired
    QuizNuggetRepository quizNuggetRepository;

    @RequestMapping(
            value = "/api/quizes/csv",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {

        List<Quiz> quizList = new ArrayList<Quiz>();
        Map<String, List<String[]>> result;
        try{
            result = CSVQuiz.parse(file);
        } catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        for (String[] value: result.get("ROWS")) {
            Quiz model = new Quiz();
            model.setName(value[0]);
            model.setDescription(value[1]);

            quizList.add(model);
        }

        try {
            quizRepository.save(quizList);
        } catch(Exception e) {
            return new ResponseEntity<String>("Could not save to DB", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>("File successfully uploaded", HttpStatus.CREATED);

    }

    @RequestMapping(
            value = "/api/quizes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        return new ResponseEntity<>(quizRepository.save(quiz), HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "/api/quizes/{quizId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<String> deleteQuiz(@PathVariable(value="quizId") Long quizId) {
        if (!this.quizRepository.exists(quizId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        quizRepository.delete(quizId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/quizes",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseStatus( value = HttpStatus.OK )
    public ResponseEntity<Quiz> updateQuiz(@RequestBody Quiz quiz) {
        if (!quizRepository.exists(quiz.getId())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(quizRepository.save(quiz), HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/quizes/nuggets",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<HashMap<String, Object>> createQuizNugget(@RequestBody HashMap<String, Object> myQuizNugget) {
        HashMap<String, Object> newMyQuizNugget = null;
        try {
            newMyQuizNugget = adminQuizHandler.createAndValidateQuizNugget(myQuizNugget);
        } catch (FormValidator.FormException exc) {
            return new ResponseEntity<>(exc.getErrMap(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(newMyQuizNugget, HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "/api/quizes/nuggets/list",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<?> createQuizNuggets(@RequestBody List<HashMap<String, Object>> myQuizNuggets) {
        List<HashMap<String, Object>> newMyQuizNuggets = new ArrayList<>();
        try {
            for (HashMap<String, Object> quizNugget : myQuizNuggets) {
                newMyQuizNuggets.
                        add(adminQuizHandler.createAndValidateQuizNugget(quizNugget));
            }
        } catch (FormValidator.FormException exc) {
            return new ResponseEntity<>(exc.getErrMap(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Iterable<HashMap<String, Object>>>(newMyQuizNuggets, HttpStatus.CREATED);
    }


    @RequestMapping(
            value = "/api/quizes/nuggets",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<HashMap<String, Object>> updateQuizNugget(@RequestBody HashMap<String, Object> myQuizNugget) {
        try {
            adminQuizHandler.updateAndValidateQuizNugget(myQuizNugget);
        } catch (FormValidator.FormException exc) {
            return new ResponseEntity(exc.getErrMap(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(myQuizNugget, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/quizes/nuggets/{quizNuggetId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<String> deleteQuizNugget(@PathVariable(value="quizNuggetId") Long quizNuggetId) {
        if (!this.quizNuggetRepository.exists(quizNuggetId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        quizNuggetRepository.delete(quizNuggetId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}