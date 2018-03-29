package se.kits.gakusei.gakuseiadmin.unit.controllers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.gakuseiadmin.content.AdminLessonRepository;
import se.kits.gakusei.gakuseiadmin.content.AdminNuggetRepository;
import se.kits.gakusei.content.repository.WordTypeRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static se.kits.gakusei.gakuseiadmin.tools.AdminTestTools.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AdminLessonControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private AdminLessonRepository lessonRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private WordTypeRepository wordTypeRepository;

    @Autowired
    private AdminNuggetRepository nuggetRepository;

    private MockMvc mockMvc;

    private Lesson lessonWithoutNuggets;
    private Lesson lessonWithNuggets;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        List<Book> books = new ArrayList<>();
        Book book = bookRepository.save(generateTestBook("book"));
        books.add(book);
        WordType wordType = wordTypeRepository.save(generateTestWordType("type"));
        Nugget nugget = nuggetRepository.save(generateNugget(wordType, books));
        List<Nugget> nuggets = new ArrayList<>();
        nuggets.add(nugget);
        lessonWithNuggets = generateLesson("nuggetLesson", nuggets);
        lessonWithoutNuggets = generateLesson("lesson", new ArrayList<>());

    }

    @After
    public void tearDown() {
        lessonRepository.deleteAll();
        nuggetRepository.deleteAll();
        bookRepository.deleteAll();
        wordTypeRepository.deleteAll();
    }

    @Test
    public void testCreateLessonWithoutNuggetsOk() throws Exception {
        String lessonString = generateLessonString(lessonWithoutNuggets);

        mockMvc.perform(post("/api/lessons")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(lessonString))
                .andExpect(status().isCreated());

        Assert.assertTrue(lessonRepository.count() == 1);
    }

    @Test
    public void testCreateLessonWithNuggetsOk() throws Exception {
        String lessonString = generateLessonString(lessonWithNuggets);
        mockMvc.perform(post("/api/lessons")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(lessonString))
                .andExpect(status().isCreated());

        Assert.assertTrue(lessonRepository.count() == 1);
    }

    @Test
    public void testCreateLessonBadRequest() throws Exception {
        lessonRepository.save(lessonWithoutNuggets);
        String lessonString = generateLessonString(lessonWithoutNuggets);

        mockMvc.perform(post("/api/lessons")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(lessonString))
                .andExpect(status().isBadRequest());

        Assert.assertTrue(lessonRepository.count() == 1);
    }

    @Test
    public void testDeleteLessonOk() throws Exception {
        Lesson savedLesson = lessonRepository.save(lessonWithoutNuggets);

        mockMvc.perform(delete("/api/lessons/" + savedLesson.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        Assert.assertTrue(lessonRepository.count() == 0);
    }

    @Test
    public void testDeleteLessonNotFound() throws Exception {
        Lesson savedLesson = lessonRepository.save(lessonWithoutNuggets);
        lessonRepository.delete(savedLesson.getId());

        mockMvc.perform(delete("/api/lessons/" + savedLesson.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLessonPage() throws Exception {
        lessonRepository.save(lessonWithoutNuggets);
        int offset = 0;
        int pageSize = 5;

        String requestUrl = String.format("/api/lessons/page/%d?pageSize=%d", offset, pageSize);
        mockMvc.perform(get(requestUrl)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"totalElements\":1")));

    }
}
