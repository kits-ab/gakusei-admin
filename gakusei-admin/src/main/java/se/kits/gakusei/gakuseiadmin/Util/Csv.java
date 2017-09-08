package se.kits.gakusei.gakuseiadmin.Util;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.gakuseiadmin.content.AdminQuizRepository;

import java.io.IOException;
import java.io.StringReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.IOException;
import java.util.Map;

public class Csv {

    /**
     * From a Mutlipart file upload, parse the CSV and return a map with its headers and rows.
     *
     * @param file
     * @return Map
     */
    public static Map<String, List<String[]>> parse(MultipartFile file) {

        CsvParserSettings settings = new CsvParserSettings();
        RowListProcessor rowProcessor = new RowListProcessor();

        settings.setLineSeparatorDetectionEnabled(true);
        settings.setRowProcessor(rowProcessor);
        settings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(settings);

        Map<String, List<String[]>> result = new HashMap<String, List<String[]>>();

        try {
            parser.parse(file.getInputStream());
        } catch(IOException e) {

            List err = new ArrayList<String>();
            err.add("CSV file could not be parsed");
            result.put("ERROR", err);

            return result;
        }

        List<String[]> headerList = new ArrayList<String[]>();

        String[] headers = rowProcessor.getHeaders();
        List<String[]> rows = rowProcessor.getRows();

        headerList.add(headers);

        result.put("HEADERS", headerList);
        result.put("ROWS", rows);

        return result;

    }

}
