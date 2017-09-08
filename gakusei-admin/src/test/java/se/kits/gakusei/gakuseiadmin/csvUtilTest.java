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
public class csvUtilTest {


    @Test
    public void testCSvParse() {

        FileInputStream fip;

        try {

            fip = new FileInputStream(new File("src/test/resources/csv/QuizCsvShouldPass.csv"));
            MultipartFile mpf = new MockMultipartFile("file", fip);

            Map<String, List<String[]>> result = QuizCsv.parse(mpf);

            for (String[] value: result.get("HEADERS")) {
                Assert.assertEquals("name", value[0]);
                Assert.assertEquals("description", value[1]);
            }

            int rowCounter = 1;
            for (String[] value: result.get("ROWS")) {
                Assert.assertEquals("test" + rowCounter, value[0]);
                Assert.assertEquals("desc_test" + rowCounter, value[1]);
                rowCounter++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
