package se.kits.gakusei.gakuseiadmin.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import se.kits.gakusei.content.model.IncorrectAnswers;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.model.QuizNugget;
import se.kits.gakusei.content.repository.IncorrectAnswerRepository;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.content.repository.QuizRepository;
import se.kits.gakusei.gakuseiadmin.tools.AdminTestTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminQuizControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizNuggetRepository quizNuggetRepository;

    @Autowired
    private IncorrectAnswerRepository incorrectAnswerRepository;

    private MockMvc mockMvc;

    private Quiz testQuiz;
    private String questions;
    private String questionsWithTooFewAnswers;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        testQuiz = quizRepository.save(AdminTestTools.generateQuiz("Test quiz 1"));
        questions = AdminTestTools.generateQuestionString(testQuiz, 3);
        questionsWithTooFewAnswers = AdminTestTools.generateQuestionString(testQuiz, 2);
    }

    @After
    public void tearDown() {
        AdminTestTools.tearDownQuiz(quizRepository, quizNuggetRepository, incorrectAnswerRepository);
    }

    @Test
    public void testCsvImport(){

        FileInputStream fip;

        try {
            fip = new FileInputStream(new File("src/test/resources/csv/QuizCsvShouldPass.csv"));

            MockMultipartFile mpf = new MockMultipartFile("file", fip);

            try {
                mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/quizes/csv?quizName=test&quizDescription=testDesc")
                        .file(mpf))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                System.err.println("[-] Could not mock file");
                e.printStackTrace();
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateQuiz() {
        String quizString = "{ \"name\": \"Test quiz unique\", \"description\": \"Test description\"}";

        try {
            mockMvc.perform(post("/api/quizes")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(quizString))
                    .andExpect(status().isCreated());
        } catch (Exception exc) { }

        Assert.assertTrue(quizRepository.findByName(testQuiz.getName()) != null);
    }

    @Test
    public void testUpdateQuiz() {
        String quizString = String.format("{ \"id\": \"%d\", \"name\": \"Test quiz\", \"description\": \"Test description NEW\"}",
                testQuiz.getId());

        try {
            mockMvc.perform(put(String.format("/api/quizes"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(quizString))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        Quiz quiz1 = quizRepository.findOne(testQuiz.getId());
        Assert.assertEquals(testQuiz.getId(), quiz1.getId());
        Assert.assertEquals(testQuiz.getDescription()+" NEW", quiz1.getDescription());
    }

    @Test
    public void testDeleteQuiz() {
        List<QuizNugget> quizNuggets = AdminTestTools.iterableToQuizNuggetList(quizNuggetRepository.save(AdminTestTools
                        .generateQuizNuggets(testQuiz,2)));
        quizNuggets.stream().map(quizNugget -> incorrectAnswerRepository
                .save(AdminTestTools.generateIncorrectAnswers(quizNugget, 3)));

        try {
            mockMvc.perform(delete(String.format("/api/quizes/%d", testQuiz.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        List<Long> quizNuggetIds = quizNuggets.stream()
                .map(QuizNugget::getId).collect(Collectors.toList());
        List<QuizNugget> quizNuggetsAfterDelete = AdminTestTools.iterableToQuizNuggetList(quizNuggetRepository
                .findAll(quizNuggetIds));

        List<List<IncorrectAnswers>> answersAfterDelete = new ArrayList<>();
        quizNuggetIds.stream().map(id -> answersAfterDelete.add(incorrectAnswerRepository.findByQuizNuggetId(id)));

        Assert.assertEquals(false, quizRepository.exists(testQuiz.getId()));
        Assert.assertTrue(quizNuggetsAfterDelete.isEmpty());
        Assert.assertTrue(answersAfterDelete.isEmpty());
    }

    @Test
    public void testCreateQuizNuggetsOK() throws Exception {
        try {
            mockMvc.perform(post(String.format("/api/quizes/nuggets/list"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(questions))
                    .andExpect(status().isCreated());
        } catch (Exception exc) { }
    }

    @Test
    public void testCreateQuizNuggetsBadRequest() throws Exception {
        try {
            mockMvc.perform(post(String.format("/api/quizes/nuggets/list"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(questionsWithTooFewAnswers))
                    .andExpect(status().isBadRequest());
        } catch (Exception exc) { }
    }

    @Test
    public void testDeleteQuizNuggetsOk() throws Exception {
        QuizNugget quizNugget = quizNuggetRepository.save(AdminTestTools.generateQuizNugget(testQuiz));
        incorrectAnswerRepository.save(AdminTestTools.generateIncorrectAnswers(quizNugget, 3));

        try {
            mockMvc.perform(delete(String.format("/api/quizes/nuggets/%d", quizNugget.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        Assert.assertTrue(incorrectAnswerRepository.findByQuizNuggetId(quizNugget.getId()).isEmpty());
        Assert.assertFalse(quizNuggetRepository.exists(quizNugget.getId()));
    }

    public void testCreateIncorrectAnswerOK() throws Exception {
        QuizNugget qn = quizNuggetRepository.save(AdminTestTools.generateQuizNugget(testQuiz));
        IncorrectAnswers ia = AdminTestTools.generateIncorrectAnswer(qn);

        String incorrectAnswerString = new ObjectMapper().writeValueAsString(ia);

        try {
            mockMvc.perform((post("/api/quizes/nuggets/incorrectAnswers"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(incorrectAnswerString))
                    .andExpect(status().isCreated());
        } catch (Exception e) { }
    }

    @Test
    public void testCreateIncorrectAnswerQuizNotFound() throws Exception {
        IncorrectAnswers ia = AdminTestTools.generateIncorrectAnswer(new QuizNugget());
        String incorrectAnswerString = new ObjectMapper().writeValueAsString(ia);

        try {
            mockMvc.perform((post("/api/quizes/nuggets/incorrectAnswers"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(incorrectAnswerString))
                    .andExpect(status().isNotFound());
        } catch (Exception e) { }

        AdminTestTools.tearDownQuiz(quizRepository, quizNuggetRepository, incorrectAnswerRepository);
    }

    @Test
    public void testDeleteIncorrectAnswerOK() throws Exception {
        QuizNugget qn = quizNuggetRepository.save(AdminTestTools.generateQuizNugget(testQuiz));
        IncorrectAnswers ia = incorrectAnswerRepository.save(AdminTestTools.generateIncorrectAnswer(qn));

        try {
            mockMvc.perform(delete("/api/quizes/nuggets/incorrectAnswers/" + ia.getId())).andExpect(status().isOk());
        } catch (Exception e) { }
    }

    @Test
    public void testDeleteIncorrectAnswerNotFound() throws Exception {
        QuizNugget qn = quizNuggetRepository.save(AdminTestTools.generateQuizNugget(testQuiz));
        IncorrectAnswers ia = AdminTestTools.generateIncorrectAnswer(qn);

        try {
            mockMvc.perform(delete("/api/quizes/nuggets/incorrectAnswers/" + ia.getId())).andExpect(status().isNotFound());
        } catch (Exception e) { }
    }
}
