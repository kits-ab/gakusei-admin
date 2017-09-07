package se.kits.gakusei.gakuseiadmin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.gakuseiadmin.Controllers.QuizAdminController;
import se.kits.gakusei.gakuseiadmin.content.AdminQuizRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


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

    @Before
    public void setUp() throws Exception {
        adminQuizController = new QuizAdminController();
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
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

            int counter = 1;
            for (Quiz quiz: testQuiz) {
                Assert.assertEquals("test"+ counter, quiz.getName());
                Assert.assertEquals("desc_test"+ counter, quiz.getDescription());
                counter++;
            }


        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }


    }

}
