package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.gakuseiadmin.content.AdminWordTypeRepository;

@RestController
public class AdminWordTypeController {

    @Autowired
    private AdminWordTypeRepository adminWordTypeRepository;

    @RequestMapping(
            value="api/wordtype/create",
            method= RequestMethod.POST,
            produces= MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<?> createWordType(@RequestBody String type) {
        WordType wordType = new WordType();
        wordType.setType(type);

        if (adminWordTypeRepository.findByType(type) == null) {
            return new ResponseEntity<WordType>(adminWordTypeRepository.save(wordType), HttpStatus.CREATED);
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
        Iterable<WordType> types = adminWordTypeRepository.findAll();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

    @RequestMapping(
            value="api/wordtype/{type}",
            method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<WordType> getWordType(@PathVariable(value="type") String type) {
        WordType wordType = adminWordTypeRepository.findByType(type);
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
        WordType wordType = adminWordTypeRepository.findByType(oldType);
        if (wordType != null) {
            wordType.setType(newType);
            return new ResponseEntity<>(adminWordTypeRepository.save(wordType), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value="api/wordtype/{type}/delete",
            method= RequestMethod.DELETE
    )
    public ResponseEntity<WordType> deleteWordType(@PathVariable(value="type") String type) {
        WordType wordType = adminWordTypeRepository.findByType(type);
        if (wordType != null) {
            adminWordTypeRepository.delete(wordType);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}