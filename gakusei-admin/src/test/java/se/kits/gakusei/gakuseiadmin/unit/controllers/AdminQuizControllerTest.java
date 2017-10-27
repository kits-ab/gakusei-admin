package se.kits.gakusei.gakuseiadmin.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.List;


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

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        testQuiz = new Quiz();
        testQuiz.setName("Test quiz");
        testQuiz.setDescription("Test description");
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

        try {
            mockMvc.perform(delete(String.format("/api/quizes/%d", quiz.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        Assert.assertEquals(false, quizRepository.exists(testQuiz.getId()));
    }

    @Test
    public void testCreateQuizNuggetsOK() throws Exception {
        Quiz quiz = quizRepository.save(testQuiz);
        List<HashMap<String, Object>> questions = new ArrayList<>();
        // create questions first after testQuiz is saved to make sure we have the correct quiz id
        questions.add(AdminTestTools.createQuestion(quiz, 3));
        String questionsString = new ObjectMapper().writeValueAsString(questions);


        try {
            mockMvc.perform(post(String.format("/api/quizes/nuggets/list"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(questionsString))
                    .andExpect(status().isCreated());
        } catch (Exception exc) { }
    }

    @Test
    public void testCreateQuizNuggetsBadRequest() throws Exception {
        Quiz quiz = quizRepository.save(testQuiz);
        List<HashMap<String, Object>> questions = new ArrayList<>();
        // create questions first after testQuiz is saved to make sure we have the correct quiz id
        // create question with not enough incorrect answers provided
        questions.add(AdminTestTools.createQuestion(quiz,2));
        String questionsString = new ObjectMapper().writeValueAsString(questions);


        try {
            mockMvc.perform(post(String.format("/api/quizes/nuggets/list"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(questionsString))
                    .andExpect(status().isBadRequest());
        } catch (Exception exc) { }

    }

    @Test
    public void testCreateIncorrectAnswerOK() throws Exception {
        testQuiz = quizRepository.save(testQuiz);

        QuizNugget qn = new QuizNugget();
        qn.setCorrectAnswer("Answer");
        qn.setQuestion("Question");
        qn.setQuiz(testQuiz);
        qn = quizNuggetRepository.save(qn);

        IncorrectAnswers ia = new IncorrectAnswers();
        ia.setIncorrectAnswer("Incorrect");
        ia.setQuizNugget(qn);

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
        IncorrectAnswers ia = new IncorrectAnswers();
        ia.setIncorrectAnswer("Incorrect");
        ia.setQuizNugget(new QuizNugget());

        String incorrectAnswerString = new ObjectMapper().writeValueAsString(ia);

        try {
            mockMvc.perform((post("/api/quizes/nuggets/incorrectAnswers"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(incorrectAnswerString))
                    .andExpect(status().isNotFound());
        } catch (Exception e) { }
    }

    @Test
    public void testDeleteIncorrectAnswerOK() throws Exception {
        testQuiz = quizRepository.save(testQuiz);

        QuizNugget qn = new QuizNugget();
        qn.setCorrectAnswer("Answer");
        qn.setQuestion("Question");
        qn.setQuiz(testQuiz);
        qn = quizNuggetRepository.save(qn);

        IncorrectAnswers ia = new IncorrectAnswers();
        ia.setIncorrectAnswer("Incorrect");
        ia.setQuizNugget(qn);
        ia = incorrectAnswerRepository.save(ia);

        try {
            mockMvc.perform(delete("/api/quizes/nuggets/incorrectAnswers/" + ia.getId())).andExpect(status().isOk());
        } catch (Exception e) { }
    }

    @Test
    public void testDeleteIncorrectAnswerNotFound() throws Exception {
        testQuiz = quizRepository.save(testQuiz);

        QuizNugget qn = new QuizNugget();
        qn.setCorrectAnswer("Answer");
        qn.setQuestion("Question");
        qn.setQuiz(testQuiz);
        qn = quizNuggetRepository.save(qn);

        IncorrectAnswers ia = new IncorrectAnswers();
        ia.setIncorrectAnswer("Incorrect");
        ia.setQuizNugget(qn);

        try {
            mockMvc.perform(delete("/api/quizes/nuggets/incorrectAnswers/" + ia.getId())).andExpect(status().isNotFound());
        } catch (Exception e) { }
    }
}
