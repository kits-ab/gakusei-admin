package se.kits.gakusei.gakuseiadmin.utility;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static org.junit.Assert.*;

public class CSVToDatabaseTest {

    String testString = "Year,Make,Model,Description,Price\n" +
            "1997,Ford,E350,\"ac, abs, moon\",3000.00\n" +
            "1999,Chevy,\"Venture \"\"Extended Edition\"\"\",\"\",4900.00\n" +
            "   \n" +
            "# Look, a multi line value. And blank rows around it!\n" +
            "     \n" +
            "1996,Jeep,Grand Cherokee,\"MUST SELL!\n" +
            "air, moon roof, loaded\",4799.00\n" +
            "1999,Chevy,\"Venture \"\"Extended Edition, Very Large\"\"\",,5000.00\n" +
            ",,\"Venture \"\"Extended Edition\"\"\",\"\",4900.00";

    @Test
    public void testParser(){
        FileInputStream fip;

        try {
            fip = new FileInputStream(new File("src/test/resources/csv/NuggetCsvShouldPass.csv"));
            MultipartFile mpf = new MockMultipartFile("file", fip);
            CSVToDatabase csvParser = new CSVToDatabase(mpf);
            assertEquals(true, csvParser.parse());
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }


    }

}