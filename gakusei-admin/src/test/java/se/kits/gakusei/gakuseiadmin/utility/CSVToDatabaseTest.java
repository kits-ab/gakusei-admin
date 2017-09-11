package se.kits.gakusei.gakuseiadmin.utility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.content.repository.NuggetRepository;
import se.kits.gakusei.content.repository.WordTypeRepository;
import se.kits.gakusei.gakuseiadmin.Controllers.FileUploadController;

import java.io.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CSVToDatabaseTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private NuggetRepository nuggetRepository;

    @Autowired
    private WordTypeRepository wordTypeRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @InjectMocks
    FileUploadController fileUploadController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testUploadPass() {
        FileInputStream fip;

        try {
            fip = new FileInputStream(new File("src/test/resources/csv/NuggetCsvShouldPass.csv"));

            MockMultipartFile mpf = new MockMultipartFile("file", fip);

            try {
                mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/nugget/import/csv")
                        .file(mpf))
                        .andExpect(status().is(200));
            } catch (Exception e) {
                System.err.println("Mock file goof");
                e.printStackTrace();
            }

        } catch (IOException e){
            e.printStackTrace();

        }

    }

    @Test
    public void testUploadFail() {
        FileInputStream fip;

        try {
            fip = new FileInputStream(new File("src/test/resources/csv/NuggetCsvShouldFail.csv"));

            MockMultipartFile mpf = new MockMultipartFile("file", fip);

            try {
                mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/nugget/import/csv")
                        .file(mpf))
                        .andExpect(status().is(400));
            } catch (Exception e) {
                System.err.println("Mock file goof");
                e.printStackTrace();
            }

        } catch (IOException e){
            e.printStackTrace();

        }

    }



}