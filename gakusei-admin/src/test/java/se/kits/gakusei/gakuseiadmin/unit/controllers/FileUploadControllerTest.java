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
import org.springframework.mock.web.MockMultipartFile;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.content.repository.NuggetRepository;
import se.kits.gakusei.gakuseiadmin.content.AdminWordTypeRepository;
import se.kits.gakusei.gakuseiadmin.controllers.FileUploadController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadControllerTest {

    @InjectMocks
    FileUploadController fileUploadController;

    @Mock
    LessonRepository lessonRepository;

    @Mock
    AdminWordTypeRepository adminWordTypeRepository;

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

        Mockito.when(adminWordTypeRepository.findByType("Test type")).thenReturn(wordType);
        Mockito.when(lessonRepository.findByName("Test lesson")).thenReturn(lesson);
        Mockito.when(bookRepository.findByTitle("Test title")).thenReturn(book);
    }

    @Test
    public void testBasicCSVNugget(){
        runPassingTest("001");
    }

    @Test
    public void testNuggetWithSeveralLessons(){
        runPassingTest("002");
    }

    @Test
    public void testManyInputs(){
        runPassingTest("003");
    }

    @Test
    public void testBasicFailingCSVNugget(){
        runFailingTest("001");
    }

    @Test
    public void testWrongBookNugget(){
        runFailingTest("002");
    }

    @Test
    public void testManyInputsOneFaulty(){
        runFailingTest("003");
    }

    private void runFailingTest(String fileID){
        try {
            MockMultipartFile mpf = new MockMultipartFile("file", new FileInputStream(new File("src/test/resources/csv/NuggetCSVShouldFail" + fileID + ".csv")));

            ResponseEntity<String> re = fileUploadController.handleFileUpload(mpf);

            assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runPassingTest(String fileID){
        try {
            MockMultipartFile mpf = new MockMultipartFile("file", new FileInputStream(new File("src/test/resources/csv/NuggetCSVShouldPass" + fileID + ".csv")));

            ResponseEntity<String> re = fileUploadController.handleFileUpload(mpf);

            assertEquals(HttpStatus.CREATED, re.getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}