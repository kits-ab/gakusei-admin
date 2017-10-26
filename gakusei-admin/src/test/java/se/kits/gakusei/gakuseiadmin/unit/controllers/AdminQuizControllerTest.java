package se.kits.gakusei.gakuseiadmin.unit.controllers;

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
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


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
    private Quiz savedQuiz;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        testQuiz = new Quiz();
        testQuiz.setName("Test quiz");
        testQuiz.setDescription("Test description");

        savedQuiz = quizRepository.save(testQuiz);
        questions = AdminTestTools.generateQuestionString(savedQuiz, 3);
        questionsWithTooFewAnswers = AdminTestTools.generateQuestionString(savedQuiz, 2);
    }

    @Test
    public void testCsvImport(){

        FileInputStream fip;

        try {
            fip = new FileInputStream(new File("src/test/resources/csv/QuizCsvShouldPass.csv"));

            MockMultipartFile mpf = new MockMultipartFile("file", fip);

            try {
                mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/quizes/csv")
                        .file(mpf))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                System.err.println("[-] Could not mock file");
                e.printStackTrace();
            }

            Iterable<Quiz> testQuiz = quizRepository.findAll();

            for (int counter=1; counter<4; counter++) {
                boolean exists = false;
                for (Quiz quiz: testQuiz) {
                    if (quiz.getName().equals("test"+counter) && quiz.getDescription().equals("desc_test"+ counter)) {
                        exists = true;
                        break;
                    }
                }
                Assert.assertEquals(true, exists);
            }


        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }


    }

    @Test
    public void testCreateQuiz() {
        String quizString = "{ \"name\": \"Test quiz\", \"description\": \"Test description\"}";

        try {
            mockMvc.perform(post("/api/quizes")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(quizString))
                    .andExpect(status().isCreated());
        } catch (Exception exc) { }

        List<Quiz> quizLs = quizRepository.findByName(testQuiz.getName());
        Assert.assertTrue(quizLs.size()>0);
    }

    @Test
    public void testUpdateQuiz() {
        Quiz quiz = quizRepository.save(testQuiz);
        String quizString = String.format("{ \"id\": \"%d\", \"name\": \"Test quiz\", \"description\": \"Test description NEW\"}",
                quiz.getId());

        try {
            mockMvc.perform(put(String.format("/api/quizes"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(quizString))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        Quiz quiz1 = quizRepository.findOne(quiz.getId());
        Assert.assertEquals(quiz.getId(), quiz1.getId());
        Assert.assertEquals(quiz.getDescription()+" NEW", quiz1.getDescription());
    }

    @Test
    public void testDeleteQuiz() {
        Quiz quiz = quizRepository.save(testQuiz);
        List<QuizNugget> quizNuggets = AdminTestTools.iterableToQuizNuggetList(quizNuggetRepository.save(AdminTestTools
                        .generateQuizNuggets(quiz,2)));
        quizNuggets.stream().map(quizNugget -> incorrectAnswerRepository
                .save(AdminTestTools.generateIncorrectAnswers(quizNugget, 3)));

        try {
            mockMvc.perform(delete(String.format("/api/quizes/%d", quiz.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        List<Long> quizNuggetIds = quizNuggets.stream()
                .map(quizNugget -> quizNugget.getId()).collect(Collectors.toList());
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
        QuizNugget quizNugget = quizNuggetRepository.save(AdminTestTools.generateQuizNugget(savedQuiz));
        incorrectAnswerRepository.save(AdminTestTools.generateIncorrectAnswers(quizNugget, 3));

        try {
            mockMvc.perform(delete(String.format("/api/quizes/nuggets/%d", quizNugget.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        Assert.assertTrue(incorrectAnswerRepository.findByQuizNuggetId(quizNugget.getId()).isEmpty());
        Assert.assertFalse(quizNuggetRepository.exists(quizNugget.getId()));
    }
}
