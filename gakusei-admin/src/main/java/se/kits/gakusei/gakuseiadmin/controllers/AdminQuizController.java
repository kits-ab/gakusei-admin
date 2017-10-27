package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.content.model.IncorrectAnswers;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.model.QuizNugget;
import se.kits.gakusei.content.repository.IncorrectAnswerRepository;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.gakuseiadmin.util.csv.CSVQuiz;
import se.kits.gakusei.gakuseiadmin.util.FormValidator;
import se.kits.gakusei.gakuseiadmin.util.AdminQuizHandler;
import se.kits.gakusei.content.repository.QuizRepository;
import se.kits.gakusei.util.ParserFailureException;
import se.kits.gakusei.util.csv.CSV;
import se.kits.gakusei.util.csv.CSVQuizNugget;

import java.io.IOException;
import java.util.*;

@RestController
public class AdminQuizController {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    AdminQuizHandler adminQuizHandler;

    @Autowired
    QuizNuggetRepository quizNuggetRepository;

    @Autowired
    IncorrectAnswerRepository incorrectAnswerRepository;

    @RequestMapping(
            value = "/api/quizes/csv",
            method = RequestMethod.POST
    )
    public ResponseEntity<String> importCsv(@RequestParam("quizName") String quizName,
                                            @RequestParam("quizDescription") String quizDescription,
                                            @RequestBody MultipartFile file) {
        Quiz newQuiz = createNewQuiz(quizName, quizDescription);

        Map<String, List<String[]>> parseResult;

        try {
            parseResult = CSV.parse(file.getInputStream(), 3);
        } catch (Exception e) {
            return new ResponseEntity<>("Error reading file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        List<String[]> rows = parseResult.get("ROWS");

        ArrayList<QuizNugget> quizNuggets = new ArrayList<>();
        ArrayList<Iterable<IncorrectAnswers>> incorrectAnswers = new ArrayList<>();

        for(String[] row : rows) {
            try {
                String[] rowWithQuizInfo = appendQuizInfo(quizName, quizDescription, row);

                CSVQuizNugget csvQuizNugget = new CSVQuizNugget(rowWithQuizInfo);
                QuizNugget quizNugget = csvQuizNugget.getQuizNugget(newQuiz);
                Iterable<IncorrectAnswers> incorrectAnswer = csvQuizNugget.getIncorrectAnswers(quizNugget);

                quizNuggets.add(quizNugget);
                incorrectAnswers.add(incorrectAnswer);
            } catch (ParserFailureException pfe) {
                return new ResponseEntity<String>("Error parsing row: " + pfe.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        saveQuiz(newQuiz, quizNuggets, incorrectAnswers);

        return new ResponseEntity<String>("File successfully uploaded", HttpStatus.CREATED);

    }

    private Quiz createNewQuiz(String name, String description){
        Quiz toReturn = new Quiz();
        toReturn.setName(name);
        toReturn.setDescription(description);

        return toReturn;
    }

    private String[] appendQuizInfo(String name, String description, String[] quizNuggetRow){
        String[] appendedRow = new String[2+quizNuggetRow.length];

        appendedRow[0] = name;
        appendedRow[1] = description;

        for(int i = 0; i < quizNuggetRow.length; i++){
            appendedRow[i+2] = quizNuggetRow[i];
        }

        return appendedRow;
    }

    private void saveQuiz(Quiz quiz,
                          ArrayList<QuizNugget> quizNuggets,
                          ArrayList<Iterable<IncorrectAnswers>> incorrectAnswers){
        quizRepository.save(quiz);
        quizNuggetRepository.save(quizNuggets);
        for(Iterable<IncorrectAnswers> ia : incorrectAnswers){
            incorrectAnswerRepository.save(ia);
        }
    }

    @RequestMapping(
            value = "/api/quizes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        if (!quizRepository.findByName(quiz.getName()).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

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

    @RequestMapping(
            value = "/api/quizes/nuggets/incorrectAnswers",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<IncorrectAnswers> createIncorrectAnswer(@RequestBody IncorrectAnswers incorrectAnswers){
        if(quizNuggetRepository.exists(incorrectAnswers.getQuizNugget().getId())) {
            return new ResponseEntity<>(incorrectAnswerRepository.save(incorrectAnswers), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/api/quizes/nuggets/incorrectAnswers/{incorrectAnswerId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<String> deleteIncorrectAnswer(@PathVariable(value = "incorrectAnswerId") Long incorrectAnswerId){
        if(incorrectAnswerRepository.exists(incorrectAnswerId)) {
            incorrectAnswerRepository.delete(incorrectAnswerId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}