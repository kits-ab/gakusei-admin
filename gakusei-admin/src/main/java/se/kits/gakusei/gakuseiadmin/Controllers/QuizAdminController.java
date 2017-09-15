package se.kits.gakusei.gakuseiadmin.Controllers;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.gakuseiadmin.Util.Csv;
import se.kits.gakusei.gakuseiadmin.Util.FormValidator;
import se.kits.gakusei.gakuseiadmin.Util.QuizAdminHandler;
import se.kits.gakusei.gakuseiadmin.Util.QuizCsv;
import se.kits.gakusei.gakuseiadmin.content.AdminQuizRepository;
import se.kits.gakusei.util.QuestionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class QuizAdminController {

    @Autowired
    AdminQuizRepository quizRepository;

    @Autowired
    QuizAdminHandler quizAdminHandler;

    @Autowired
    QuizNuggetRepository quizNuggetRepository;

    @RequestMapping(
            value = "/api/quiz/import/csv",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {

        List<Quiz> quizList = new ArrayList<Quiz>();
        Map<String, List<String[]>> result;
        try{
            result = QuizCsv.parse(file);
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

        return new ResponseEntity<String>("File successfully uploaded", HttpStatus.OK);

    }

    @RequestMapping(
            value = "/api/quiz/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Quiz> createQuiz(HttpServletRequest request, @RequestBody Quiz quiz) {
        return ResponseEntity.ok(quizRepository.save(quiz));
    }

    @RequestMapping(
            value = "/api/quiz/{quizId}/delete",
            method = RequestMethod.DELETE
    )
    public ResponseEntity deleteQuiz(HttpServletRequest request, @PathVariable(value="quizId") Long quizId) {
        if (!this.quizRepository.exists(quizId))
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);

        quizRepository.delete(quizId);
        return new ResponseEntity(null, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/quiz/update",
            method = RequestMethod.PUT
    )
    @ResponseStatus( value = HttpStatus.OK )
    public ResponseEntity<HashMap<String, Object>> updateQuiz(HttpServletRequest request, @RequestBody Quiz quiz) {
        if (quizRepository.exists(quiz.getId())) {
            quizRepository.save(quiz);
            return new ResponseEntity(null, HttpStatus.OK);
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("error", "Quiz does not exist");
        return new ResponseEntity(hashMap, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(
            value = "/api/quiz/nugget/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<HashMap<String, Object>> createQuizNugget(HttpServletRequest request,
                                                                    @RequestBody HashMap<String, Object> myQuizNugget) {
        HashMap<String, Object> newMyQuizNugget = null;
        try {
            newMyQuizNugget = quizAdminHandler.createAndValidateQuizNugget(myQuizNugget);
        } catch (FormValidator.FormException exc) {
            return new ResponseEntity(exc.getErrMap(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(newMyQuizNugget, HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "/api/quiz/nugget/update",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<HashMap<String, Object>> updateQuizNugget(HttpServletRequest request,
                                                                    @RequestBody HashMap<String, Object> myQuizNugget) {
        HashMap<String, Object> newMyQuizNugget = null;
        try {
            quizAdminHandler.updateAndValidateQuizNugget(myQuizNugget);
        } catch (FormValidator.FormException exc) {
            return new ResponseEntity(exc.getErrMap(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(newMyQuizNugget, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/quiz/nugget/{quizNuggetId}/delete",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<HashMap<String, Object>> deleteQuizNugget(HttpServletRequest request,
                                                                    @PathVariable(value="quizNuggetId") Long quizNuggetId) {
        if (!this.quizNuggetRepository.exists(quizNuggetId))
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);

        HashMap<String, Object> newMyQuizNugget = null;
        this.quizNuggetRepository.delete(quizNuggetId);
        return new ResponseEntity(null, HttpStatus.OK);
    }
}