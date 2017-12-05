package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.repository.BookRepository;

@RestController
public class AdminBookController {

    @Autowired
    BookRepository bookRepository;

    @RequestMapping(
        value = "/api/books",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Book> updateBookTitle(@RequestBody Book updatedBook){
        if(!bookRepository.exists(updatedBook.getId())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(bookRepository.save(updatedBook), HttpStatus.OK);
    }

    @RequestMapping(
        value = "/api/books",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Book> createNewBook(@RequestBody String title){

        if(bookRepository.findByTitle(title) != null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Book newBook = new Book();
        newBook.setTitle(title);

        return new ResponseEntity<>(bookRepository.save(newBook), HttpStatus.CREATED);
    }

    @RequestMapping(
        value = "/api/books/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Book> deleteBook(@PathVariable(value = "id") Long id){
        if(!bookRepository.exists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        bookRepository.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
