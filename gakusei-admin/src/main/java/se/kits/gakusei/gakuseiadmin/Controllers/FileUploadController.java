package se.kits.gakusei.gakuseiadmin.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.content.repository.NuggetRepository;
import se.kits.gakusei.content.repository.WordTypeRepository;
import se.kits.gakusei.gakuseiadmin.utility.CSVToDatabase;
import se.kits.gakusei.gakuseiadmin.utility.ParserFailureException;

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
    private WordTypeRepository wordTypeRepository;

    @PostMapping("/api/nugget/import/csv")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file){

        CSVToDatabase parser = new CSVToDatabase(file, lessonRepository, wordTypeRepository, bookRepository);

        try {
            List<Nugget> nuggets = parser.parse();
            nuggetRepository.save(nuggets);

            return ResponseEntity.ok().body(file.getName() + " was received!");
        } catch (ParserFailureException e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
        
    }

}
