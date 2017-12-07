package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.content.model.IncorrectAnswer;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.model.QuizNugget;
import se.kits.gakusei.content.repository.IncorrectAnswerRepository;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.gakuseiadmin.dto.QuizNuggetDTO;
import se.kits.gakusei.gakuseiadmin.util.ParseResult;
import se.kits.gakusei.gakuseiadmin.util.AdminQuizHandler;
import se.kits.gakusei.content.repository.QuizRepository;
import se.kits.gakusei.util.csv.CSV;

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
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<String>> importCsv(@RequestParam("quizName") String quizName,
                                            @RequestParam("quizDescription") String quizDescription,
                                            @RequestBody MultipartFile file) {
        ParseResult parseResult;

        Quiz quiz = adminQuizHandler.createQuiz(quizName, quizDescription);

        try {
            parseResult = new ParseResult(CSV.parse(file.getInputStream(), 3), quiz);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonList("Filen kunde inte läsas: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        if(parseResult.isSuccessful()){
            adminQuizHandler.saveQuiz(
                    quiz, parseResult.getParsedQuizNuggets(), parseResult.getParsedIncorrectAnswers());
        } else {
            return new ResponseEntity<>(parseResult.getErrors(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Collections.singletonList("Fil uppladdad!"), HttpStatus.CREATED);

    }

    @RequestMapping(
            value = "/api/quizes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        if (quizRepository.findByName(quiz.getName()) != null) {
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
        adminQuizHandler.handleDeleteQuiz(quizId);
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
    public ResponseEntity<QuizNuggetDTO> createQuizNugget(@RequestBody QuizNuggetDTO quizNuggetDTO) {
        List<IncorrectAnswer> incorrectAnswers = quizNuggetDTO.getIncorrectAnswers();
        if (incorrectAnswers.size() < 3) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        QuizNugget quizNugget = adminQuizHandler.createQuizNugget(quizNuggetDTO);
        incorrectAnswers = adminQuizHandler.createIncorrectAnswers(incorrectAnswers, quizNugget);
        quizNuggetDTO = adminQuizHandler.updateQuizNuggetDTO(quizNuggetDTO, incorrectAnswers, quizNugget.getId());

        return new ResponseEntity<>(quizNuggetDTO, HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "/api/quizes/nuggets/list",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<QuizNuggetDTO>> createQuizNuggets(@RequestBody List<QuizNuggetDTO> quizNuggetDTOs) {
        List<QuizNuggetDTO> responseList = new ArrayList<>();
        for (QuizNuggetDTO dto : quizNuggetDTOs) {
            ResponseEntity<QuizNuggetDTO> response = createQuizNugget(dto);
            if(!response.getStatusCode().is2xxSuccessful()) {
                // Stop iterating if we run into errors
                return new ResponseEntity<>(response.getStatusCode());
            }
            responseList.add(response.getBody());
        }

        return new ResponseEntity<>(responseList, HttpStatus.CREATED);
    }


    @RequestMapping(
            value = "/api/quizes/nuggets",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<QuizNuggetDTO> updateQuizNugget(@RequestBody QuizNuggetDTO quizNuggetDTO) {
        List<IncorrectAnswer> incorrectAnswers = quizNuggetDTO.getIncorrectAnswers();
        if (incorrectAnswers.size() < 3) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        QuizNugget quizNugget = adminQuizHandler.updateQuizNugget(quizNuggetDTO);
        incorrectAnswers = adminQuizHandler.createIncorrectAnswers(incorrectAnswers, quizNugget);
        quizNuggetDTO = adminQuizHandler.updateQuizNuggetDTO(quizNuggetDTO, incorrectAnswers, quizNugget.getId());

        return new ResponseEntity<>(quizNuggetDTO, HttpStatus.OK);

    }

    @RequestMapping(
            value = "/api/quizes/{quizId}/nuggets",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<QuizNuggetDTO>> getQuizNuggets(@PathVariable(value="quizId") Long quizId) {
        List<QuizNuggetDTO> quizNuggets = adminQuizHandler.getQuizNuggets(quizId);
        return new ResponseEntity<>(quizNuggets, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/quizes/nuggets/{quizNuggetId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<String> deleteQuizNugget(@PathVariable(value="quizNuggetId") Long quizNuggetId) {
        if (!quizNuggetRepository.exists(quizNuggetId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        adminQuizHandler.handleDeleteQuizNugget(quizNuggetId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/quizes/nuggets/incorrectAnswers",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<IncorrectAnswer> createIncorrectAnswer(@RequestBody IncorrectAnswer incorrectAnswer){
        if(quizNuggetRepository.exists(incorrectAnswer.getQuizNugget().getId())) {
            return new ResponseEntity<>(incorrectAnswerRepository.save(incorrectAnswer), HttpStatus.CREATED);
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