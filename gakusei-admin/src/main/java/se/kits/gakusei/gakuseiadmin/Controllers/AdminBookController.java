package se.kits.gakusei.gakuseiadmin.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.controller.BookController;

@RestController
public class AdminBookController extends BookController {

    @Autowired
    BookRepository bookRepository;

    @RequestMapping(
        value = "/api/books/{oldTitle}/update/{newTitle}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Book> updateBookTitle(@PathVariable(value = "oldTitle") String oldTitle, @PathVariable(value = "newTitle") String newTitle){
        Book toBeUpdated = bookRepository.findByTitle(oldTitle);

        if(toBeUpdated == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        toBeUpdated.setTitle(newTitle);
        return new ResponseEntity<>(bookRepository.save(toBeUpdated), HttpStatus.OK); // Automagically updates current book
    }

    @RequestMapping(
        value = "/api/books/{title}/create",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Book> createNewBook(@PathVariable(value = "title") String title){

        if(bookRepository.findByTitle(title) != null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Book newBook = new Book();
        newBook.setTitle(title);

        return new ResponseEntity<>(bookRepository.save(newBook), HttpStatus.OK);
    }

    @RequestMapping(
        value = "/api/books/{title}/delete",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Book> deleteBook(@PathVariable(value = "title") String title){
        Book toBeDeleted = bookRepository.findByTitle(title);

        if(toBeDeleted == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        bookRepository.delete(toBeDeleted);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
