package se.kits.gakusei.gakuseiadmin.Controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.content.repository.NuggetRepository;
import se.kits.gakusei.content.repository.WordTypeRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadControllerTest {

    @InjectMocks
    FileUploadController fileUploadController;

    @Mock
    LessonRepository lessonRepository;

    @Mock
    WordTypeRepository wordTypeRepository;

    @Mock
    BookRepository bookRepository;

    @Mock
    NuggetRepository nuggetRepository;

    private WordType wordType;
    private Book book;
    private Lesson lesson;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        wordType = new WordType();
        wordType.setType("Test type");

        book = new Book();
        book.setTitle("Test title");

        lesson = new Lesson();
        lesson.setName("Test lesson");

        Mockito.when(wordTypeRepository.findByType("Test type")).thenReturn(wordType);
        Mockito.when(lessonRepository.findByName("Test lesson")).thenReturn(lesson);
        Mockito.when(bookRepository.findByTitle("Test title")).thenReturn(book);
    }

    @Test
    public void testBasicCSVNugget(){

        try {
            MockMultipartFile mpf = new MockMultipartFile("file", new FileInputStream(new File("src/test/resources/csv/NuggetCSVShouldPass001.csv")));

            ResponseEntity<String> re = fileUploadController.handleFileUpload(mpf);

            assertEquals(200, re.getStatusCodeValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBasicFailingCSVNugget(){

        try {
            MockMultipartFile mpf = new MockMultipartFile("file", new FileInputStream(new File("src/test/resources/csv/NuggetCSvShouldFail001.csv")));

            ResponseEntity<String> re = fileUploadController.handleFileUpload(mpf);

            assertEquals(400, re.getStatusCodeValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}