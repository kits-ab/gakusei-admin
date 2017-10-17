package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.content.repository.NuggetRepository;
import se.kits.gakusei.gakuseiadmin.content.AdminWordTypeRepository;
import se.kits.gakusei.gakuseiadmin.util.csv.CSVToDatabase;
import se.kits.gakusei.util.ParserFailureException;

import java.util.List;

@RestController
public class FileUploadController {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private NuggetRepository nuggetRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AdminWordTypeRepository adminWordTypeRepository;

    @PostMapping("/api/nuggets/csv")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file){

        CSVToDatabase parser = new CSVToDatabase(file, lessonRepository, adminWordTypeRepository, bookRepository);

        try {
            List<Nugget> nuggets = parser.parse();
            nuggetRepository.save(nuggets);

            return new ResponseEntity<>(file.getName() + " was received!", HttpStatus.CREATED);
        } catch (ParserFailureException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

}
