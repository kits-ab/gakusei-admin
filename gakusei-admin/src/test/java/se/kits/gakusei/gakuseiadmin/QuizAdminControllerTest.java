package se.kits.gakusei.gakuseiadmin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
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
import se.kits.gakusei.gakuseiadmin.controllers.QuizAdminController;
import se.kits.gakusei.gakuseiadmin.content.AdminQuizRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class QuizAdminControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private AdminQuizRepository adminQuizRepository;

    private MockMvc mockMvc;

    @InjectMocks
    private QuizAdminController adminQuizController;

    private Quiz testQuiz;

    @Before
    public void setUp() throws Exception {
        adminQuizController = new QuizAdminController();
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

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
                mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/quiz/import/csv")
                        .file(mpf))
                        .andExpect(status().is(200));
            } catch (Exception e) {
                System.err.println("[-] Could not mock file");
                e.printStackTrace();
            }

            List<Quiz> testQuiz = adminQuizRepository.findAll();

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
            mockMvc.perform(post("/api/quiz/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(quizString))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        List<Quiz> quizLs = this.adminQuizRepository.findByName(this.testQuiz.getName());
        Assert.assertTrue(quizLs.size()>0);
    }

    @Test
    public void testUpdateQuiz() {
        Quiz quiz = this.adminQuizRepository.save(this.testQuiz);
        String quizString = String.format("{ \"id\": \"%d\", \"name\": \"Test quiz\", \"description\": \"Test description NEW\"}",
                quiz.getId());

        try {
            mockMvc.perform(put("/api/quiz/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(quizString))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        Quiz quiz1 = this.adminQuizRepository.findOne(quiz.getId());
        Assert.assertEquals(quiz.getId(), quiz1.getId());
        Assert.assertEquals(quiz.getDescription()+" NEW", quiz1.getDescription());
    }

    @Test
    public void testDeleteQuiz() {
        Quiz quiz = this.adminQuizRepository.save(this.testQuiz);

        try {
            mockMvc.perform(delete(String.format("/api/quiz/%d/delete", quiz.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) { }

        Assert.assertEquals(false, this.adminQuizRepository.exists(this.testQuiz.getId()));
    }
}
