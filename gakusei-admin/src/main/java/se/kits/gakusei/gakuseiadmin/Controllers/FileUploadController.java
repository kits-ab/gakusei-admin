package se.kits.gakusei.gakuseiadmin.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.gakuseiadmin.utility.CSVToDatabase;

@RestController
public class FileUploadController {

    @PostMapping("/api/nugget/import/csv")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file){

        CSVToDatabase parser = new CSVToDatabase(file);

        if(parser.parse()){
            return ResponseEntity.ok().body(file.getName() + " was received!");
        } else {
            return ResponseEntity.badRequest().body("Malformed CSV");
        }

    }

}
