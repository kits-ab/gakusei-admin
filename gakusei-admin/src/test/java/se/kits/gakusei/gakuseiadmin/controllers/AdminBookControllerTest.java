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

    private String oldTitle;
    private String newTitle;
    private String invalidTitle;

    private Book testBook;

    @Before
    public void setUp(){

        MockitoAnnotations.initMocks(this);

        oldTitle = "Old title";
        newTitle = "New title";
        invalidTitle = "Invalid title";

        testBook = new Book();
        testBook.setTitle(oldTitle);

    }

    @Test
    public void testBasicPUT(){

        Mockito.when(bookRepository.findByTitle(oldTitle)).thenReturn(testBook);
        testBook.setTitle(newTitle);
        Mockito.when(bookRepository.save(testBook)).thenReturn(testBook);

        ResponseEntity<Book> re = adminBookController.updateBookTitle(oldTitle, newTitle);

        assertEquals(200, re.getStatusCodeValue());
        assertEquals(testBook, re.getBody());

    }

    @Test
    public void testBasicPOST(){

    }

    @Test
    public void testBasicDELETE(){

    }

}