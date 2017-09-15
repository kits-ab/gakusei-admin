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
import se.kits.gakusei.gakuseiadmin.Util.Csv;
import se.kits.gakusei.gakuseiadmin.Util.QuizCsv;
import se.kits.gakusei.gakuseiadmin.content.AdminQuizRepository;
import se.kits.gakusei.util.QuestionHandler;

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
}