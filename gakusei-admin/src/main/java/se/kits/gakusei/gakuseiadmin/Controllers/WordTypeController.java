package se.kits.gakusei.gakuseiadmin.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.gakuseiadmin.content.WordTypeRepository;

@RestController
public class WordTypeController {

    @Autowired
    private WordTypeRepository wordTypeRepository;

    @RequestMapping(
            value="api/wordtype/create",
            method= RequestMethod.POST,
            produces= MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<?> createWordType(@RequestBody String type) {
        WordType wordType = new WordType();
        wordType.setType(type);

        if (wordTypeRepository.findByType(type) == null) {
            return new ResponseEntity<WordType>(wordTypeRepository.save(wordType), HttpStatus.CREATED);
        }

        String errorMessage = "Word type " + type + " already exists";
        return new ResponseEntity<String>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(
            value="api/wordtype",
            method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Iterable<WordType>> getWordTypes() {
        Iterable<WordType> types = wordTypeRepository.findAll();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

    @RequestMapping(
            value="api/wordtype/{type}",
            method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<WordType> getWordType(@PathVariable(value="type") String type) {
        WordType wordType = wordTypeRepository.findByType(type);
        if (wordType != null) {
            return new ResponseEntity<>(wordType, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value="api/wordtype/{oldType}/update/{newType}",
            method= RequestMethod.PUT
    )
    public ResponseEntity<WordType> updateWordType(@PathVariable(value="oldType") String oldType, @PathVariable(value="newType") String newType) {
        WordType wordType = wordTypeRepository.findByType(oldType);
        if (wordType != null) {
            wordType.setType(newType);
            return new ResponseEntity<>(wordTypeRepository.save(wordType), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value="api/wordtype/{type}/delete",
            method= RequestMethod.DELETE
    )
    public ResponseEntity<WordType> deleteWordType(@PathVariable(value="type") String type) {
        WordType wordType = wordTypeRepository.findByType(type);
        if (wordType != null) {
            wordTypeRepository.delete(wordType);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
