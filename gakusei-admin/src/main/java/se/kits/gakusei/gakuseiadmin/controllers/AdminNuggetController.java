package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.content.repository.NuggetRepository;
import se.kits.gakusei.gakuseiadmin.content.AdminNuggetRepository;

import java.util.List;

@RestController
public class AdminNuggetController {

    @Autowired
    private NuggetRepository nuggetRepository;

    @Autowired
    private AdminNuggetRepository adminNuggetRepository;

    @Autowired
    private BookRepository bookRepository;

    @RequestMapping(
            value = "/api/nuggets",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity createNugget(@RequestBody Nugget nugget) {
        return new ResponseEntity<Nugget>(nuggetRepository.save(nugget), HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "/api/nuggets",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Nugget> updateNugget(@RequestBody Nugget nugget){
        if(nuggetRepository.equals(nugget.getId())){
            return new ResponseEntity<Nugget>(nuggetRepository.save(nugget), HttpStatus.OK);
        }
        return new ResponseEntity<Nugget>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value = "/api/nuggets/{offset}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Page<Nugget>> getNuggetPage(@PathVariable(value = "offset") int offset,
                                                      @RequestParam(value = "pageSize") int pageSize) {
        Pageable pageRequest = new PageRequest(offset, pageSize);
        return new ResponseEntity<>(adminNuggetRepository.findAll(pageRequest), HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/nuggets/{offset}/search",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Page<Nugget>> getNuggetPageFilterBySwedishAndWordTypeAndBooks(
            @PathVariable(value = "offset") int offset,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(value = "swedish") String swedish,
            @RequestParam(value = "wordTypeId", required = false) Long wordTypeId,
            @RequestParam(value = "bookIds", required = false) List<Long> bookIds) {
        Pageable pageRequest = new PageRequest(offset, pageSize);
        Iterable<Book> books = bookRepository.findAll(bookIds);

        if (wordTypeId == null && bookIds == null) {
            return new ResponseEntity<>(
                    adminNuggetRepository.findBySwedishContainingIgnoreCase(swedish, pageRequest), HttpStatus.OK);
        } else if (wordTypeId == null) {
            return new ResponseEntity<>(
                    adminNuggetRepository.findBySwedishContainingIgnoreCaseAndBooksIn(
                            swedish, books, pageRequest), HttpStatus.OK);
        } else if (bookIds == null) {
            return new ResponseEntity<>(
                    adminNuggetRepository.findBySwedishContainingIgnoreCaseAndWordTypeId(
                            swedish, wordTypeId, pageRequest), HttpStatus.OK);
        }

        return new ResponseEntity<>(
                adminNuggetRepository.findBySwedishContainingIgnoreCaseAndWordTypeIdAndBooksIn(
                        swedish, wordTypeId, books, pageRequest), HttpStatus.OK);

    }

    @RequestMapping(
            value = "/api/nuggets/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<String> deleteNugget(@PathVariable String id) {
        if (nuggetRepository.exists(id)) {
            nuggetRepository.delete(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        return new ResponseEntity<String>("Nugget does not exist", HttpStatus.NOT_FOUND);
    }
}
