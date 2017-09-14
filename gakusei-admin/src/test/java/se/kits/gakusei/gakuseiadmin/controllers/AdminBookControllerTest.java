package se.kits.gakusei.gakuseiadmin.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.gakuseiadmin.Controllers.AdminBookController;

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

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        title = "Title";
        newTitle = "New title";
        invalidTitle = "Invalid title";

        testBook = new Book();
        testBook.setTitle(title);
    }

    @Test
    public void testBasicPUT(){
        Mockito.when(bookRepository.findByTitle(title)).thenReturn(testBook);
        testBook.setTitle(newTitle);
        Mockito.when(bookRepository.save(testBook)).thenReturn(testBook);

        ResponseEntity<Book> re = adminBookController.updateBookTitle(title, newTitle);

        assertEquals(200, re.getStatusCodeValue());
        assertEquals(testBook, re.getBody());
    }

    @Test
    public void testFailingPUT(){
        Mockito.when(bookRepository.findByTitle(invalidTitle)).thenReturn(null);

        ResponseEntity<Book> re = adminBookController.updateBookTitle(invalidTitle, newTitle);

        assertEquals(400, re.getStatusCodeValue());
    }

    @Test
    public void testBasicPOST(){
        Mockito.when(bookRepository.findByTitle(title)).thenReturn(null);
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(testBook);

        ResponseEntity<Book> re = adminBookController.createNewBook(title);

        assertEquals(200, re.getStatusCodeValue());
        assertEquals(testBook, re.getBody());
    }

    @Test
    public void testFailingPOST(){
        Mockito.when(bookRepository.findByTitle(invalidTitle)).thenReturn(testBook);

        ResponseEntity<Book> re = adminBookController.createNewBook(invalidTitle);

        assertEquals(400, re.getStatusCodeValue());
    }

    @Test
    public void testBasicDELETE(){
        Mockito.when(bookRepository.findByTitle(title)).thenReturn(testBook);

        ResponseEntity<Book> re = adminBookController.deleteBook(title);

        assertEquals(200, re.getStatusCodeValue());
    }

    @Test
    public void testFailingDELETE(){
        Mockito.when(bookRepository.findByTitle(invalidTitle)).thenReturn(null);

        ResponseEntity<Book> re = adminBookController.deleteBook(invalidTitle);

        assertEquals(400, re.getStatusCodeValue());
    }

}