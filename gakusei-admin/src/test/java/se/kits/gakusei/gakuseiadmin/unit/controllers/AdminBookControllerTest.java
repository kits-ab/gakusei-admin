package se.kits.gakusei.gakuseiadmin.unit.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.repository.BookRepository;

import se.kits.gakusei.gakuseiadmin.controllers.AdminBookController;
import se.kits.gakusei.gakuseiadmin.tools.AdminTestTools;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminBookControllerTest {

    @InjectMocks
    AdminBookController adminBookController;

    @Mock
    BookRepository bookRepository;

    private String title;
    private String newTitle;
    private String invalidTitle;

    private Book testBook;
    private Book updatedBook;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        title = "Title";
        newTitle = "New title";
        invalidTitle = "Invalid title";

        testBook = AdminTestTools.generateTestBook(title);
        updatedBook = AdminTestTools.updateTestBook(testBook, newTitle);

    }

    @Test
    public void testBasicPUT(){
        Mockito.when(bookRepository.exists(updatedBook.getId())).thenReturn(true);
        Mockito.when(bookRepository.save(updatedBook)).thenReturn(updatedBook);

        ResponseEntity<Book> re = adminBookController.updateBookTitle(updatedBook);

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(updatedBook, re.getBody());
    }

    @Test
    public void testFailingPUT(){
        Mockito.when(bookRepository.exists(updatedBook.getId())).thenReturn(false);

        ResponseEntity<Book> re = adminBookController.updateBookTitle(updatedBook);

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
    }

    @Test
    public void testBasicPOST(){
        Mockito.when(bookRepository.findByTitle(title)).thenReturn(null);
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(testBook);

        ResponseEntity<Book> re = adminBookController.createNewBook(title);

        assertEquals(HttpStatus.CREATED, re.getStatusCode());
        assertEquals(testBook, re.getBody());
    }

    @Test
    public void testFailingPOST(){
        Mockito.when(bookRepository.findByTitle(invalidTitle)).thenReturn(testBook);

        ResponseEntity<Book> re = adminBookController.createNewBook(invalidTitle);

        assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
    }

    @Test
    public void testBasicDELETE(){
        Mockito.when(bookRepository.exists(testBook.getId())).thenReturn(true);

        ResponseEntity<Book> re = adminBookController.deleteBook(testBook.getId());

        assertEquals(HttpStatus.OK, re.getStatusCode());
    }

    @Test
    public void testFailingDELETE(){
        Mockito.when(bookRepository.exists(testBook.getId())).thenReturn(false);

        ResponseEntity<Book> re = adminBookController.deleteBook(testBook.getId());

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
    }

}