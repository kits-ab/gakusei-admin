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
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.gakuseiadmin.content.AdminQuizRepository;
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
    private AdminQuizRepository adminQuizRepository;

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
                mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/quizes/csv")
                        .file(mpf))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                System.err.println("[-] Could not mock file");
                e.printStackTrace();
            }

            Iterable<Quiz> testQuiz = adminQuizRepository.findAll();

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

        List<Quiz> quizLs = adminQuizRepository.findByName(testQuiz.getName());
        Assert.assertTrue(quizLs.size()>0);
    }

    @Test
    public void testUpdateQuiz() {
        Quiz quiz = adminQuizRepository.save(testQuiz);
        String quizString = String.format("{ \"id\": \"%d\", \"name\": \"Test quiz\", \"description\": \"Test description NEW\"}",
                quiz.getId());

        try {
            mockMvc.perform(put(String.format("/api/quizes"))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(quizString))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        Quiz quiz1 = adminQuizRepository.findOne(quiz.getId());
        Assert.assertEquals(quiz.getId(), quiz1.getId());
        Assert.assertEquals(quiz.getDescription()+" NEW", quiz1.getDescription());
    }

    @Test
    public void testDeleteQuiz() {
        Quiz quiz = adminQuizRepository.save(testQuiz);

        try {
            mockMvc.perform(delete(String.format("/api/quizes/%d", quiz.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        Assert.assertEquals(false, adminQuizRepository.exists(testQuiz.getId()));
    }

    @Test
    public void testCreateQuizNuggetsOK() throws Exception {
        Quiz quiz = adminQuizRepository.save(testQuiz);
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
        Quiz quiz = adminQuizRepository.save(testQuiz);
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
}
