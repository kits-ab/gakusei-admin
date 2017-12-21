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
import se.kits.gakusei.content.model.Kanji;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.gakuseiadmin.content.AdminKanjiRepository;

import java.util.List;


@RestController
public class AdminKanjiController {

    @Autowired
    private AdminKanjiRepository adminKanjiRepository;

    @Autowired
    private BookRepository bookRepository;

    @RequestMapping(
            value = "/api/kanjis",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Page<Kanji>> getKanjiPage(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(value = "swedish") String swedish,
            @RequestParam(value = "bookIds", required = false) List<Long> bookIds) {
        Pageable pageRequest = new PageRequest(offset, pageSize);

        if(bookIds == null){
            return new ResponseEntity<>(
                    adminKanjiRepository.findBySwedishContainingIgnoreCase(
                            swedish, pageRequest), HttpStatus.OK);
        } else {
            Iterable<Book> books = bookRepository.findAll(bookIds);
            return new ResponseEntity<>(
                    adminKanjiRepository.findBySwedishContainingIgnoreCaseAndBooksIn(
                            swedish, books, pageRequest), HttpStatus.OK);
        }
    }

    @RequestMapping(
            value = "/api/kanjis",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Kanji> createKanji(@RequestBody Kanji kanji){
        return new ResponseEntity<>(adminKanjiRepository.save(kanji), HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "/api/kanjis",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Kanji> updateKanji(@RequestBody Kanji kanji){
        if(adminKanjiRepository.exists(kanji.getId())){
            return new ResponseEntity<>(adminKanjiRepository.save(kanji), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value = "/api/kanjis/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<String> deleteKanji(@PathVariable String id){
        if(adminKanjiRepository.exists(id)){
            adminKanjiRepository.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
