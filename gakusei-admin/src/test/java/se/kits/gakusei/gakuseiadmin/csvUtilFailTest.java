package se.kits.gakusei.gakuseiadmin;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.gakuseiadmin.Util.Csv;
import se.kits.gakusei.gakuseiadmin.Util.QuizCsv;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class csvUtilFailTest {


    @Test
    public void testCsvRowParse() {

        FileInputStream fip;

        try {

            fip = new FileInputStream(new File("src/test/resources/csv/QuizRowShouldFail.csv"));
            MultipartFile mpf = new MockMultipartFile("file", fip);
            try {
                Map<String, List<String[]>> result = QuizCsv.parse(mpf);
                Assert.fail();
            } catch (Exception e){
                Assert.assertTrue(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCsvHeadParse(){

        FileInputStream fip;

        try {

            fip = new FileInputStream(new File("src/test/resources/csv/QuizHeaderShouldFail.csv"));
            MultipartFile mpf = new MockMultipartFile("file", fip);
            try {
                Map<String, List<String[]>> result = QuizCsv.parse(mpf);
                Assert.fail();
            } catch (Exception e){
                Assert.assertTrue(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}