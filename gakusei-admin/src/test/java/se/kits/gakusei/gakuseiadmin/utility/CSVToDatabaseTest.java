package se.kits.gakusei.gakuseiadmin.utility;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
        MultipartFile mpf = new MockMultipartFile("file", "test.txt", "test/plain", testString.getBytes());
        CSVToDatabase csvParser = new CSVToDatabase(mpf);
        assertEquals(true, csvParser.parse());
    }

}