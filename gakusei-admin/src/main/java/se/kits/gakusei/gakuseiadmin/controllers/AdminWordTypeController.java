package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.content.repository.WordTypeRepository;

@RestController
public class AdminWordTypeController {

    @Autowired
    private WordTypeRepository wordTypeRepository;

    @RequestMapping(
            value="api/wordtypes",
            method= RequestMethod.POST,
            produces= MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<?> createWordType(@RequestBody String type) {
        WordType wordType = new WordType();
        wordType.setType(type);

        if (wordTypeRepository.findByType(type) != null) {
            String errorMessage = "Word type " + type + " already exists";
            return new ResponseEntity<String>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<WordType>(wordTypeRepository.save(wordType), HttpStatus.CREATED);
    }

    @RequestMapping(
            value="api/wordtypes",
            method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Iterable<WordType>> getWordTypes() {
        Iterable<WordType> types = wordTypeRepository.findAll();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

    @RequestMapping(
            value="api/wordtypes/{id}",
            method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<WordType> getWordType(@PathVariable(value="id") Long id) {
        WordType wordType = wordTypeRepository.findOne(id);
        if (wordType == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(wordType, HttpStatus.OK);
    }

    @RequestMapping(
            value="api/wordtypes",
            method= RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<WordType> updateWordType(@RequestBody WordType updatedType) {
        if (!wordTypeRepository.exists(updatedType.getId())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(wordTypeRepository.save(updatedType), HttpStatus.OK);
    }

    @RequestMapping(
            value="api/wordtypes/{id}",
            method= RequestMethod.DELETE
    )
    public ResponseEntity<String> deleteWordType(@PathVariable(value="id") Long id) {
        if (!wordTypeRepository.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        wordTypeRepository.delete(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
