package se.kits.gakusei.gakuseiadmin.utility;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class CSVToDatabaseTest {

    String testString = "[{\n" +
            "    \"swedish\": [\n" +
            "      \"servitör\"\n" +
            "    ],\n" +
            "    \"genki\": [\n" +
            "      \"13\"\n" +
            "    ],\n" +
            "    \"writing\": [\n" +
            "      \"ウエイター\"\n" +
            "    ],\n" +
            "    \"english\": [\n" +
            "      \"waiter\"\n" +
            "    ],\n" +
            "    \"reading\": [\n" +
            "      \"ウエイター\"\n" +
            "    ],\n" +
            "    \"state\": \"\",\n" +
            "    \"id\": \"12x1\",\n" +
            "    \"type\": [\n" +
            "      \"noun\"\n" +
            "    ]\n" +
            "  }]";

    @Test
    public void testParser(){
        MultipartFile mpf = new MockMultipartFile("file", "test.txt", "test/plain", testString.getBytes());
        CSVToDatabase csvParser = new CSVToDatabase(mpf);
        assertEquals(true, csvParser.parse());
    }

}