package se.kits.gakusei.gakuseiadmin.util.csv;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public class CSVQuiz {
    public static Map<String, List<String[]>> parse(MultipartFile file) throws Exception {
        Map<String, List<String[]>> parse = CSV.parse(file);
        String[] headers = parse.get("HEADERS").get(0);
        List<String[]> rows = parse.get("ROWS");
        if(headers.length != 2){
            throw new Exception("Error with headers should be: name, description");
        }
        if(!headers[0].equals("name") || !headers[1].equals("description") ){
            throw new Exception("Error with headers should be: name, description");
        }
        for(String[] row : rows){
            if(row.length != 2 && row.length != 1){
                throw new Exception("Error with rows, should either be 'name, description' or 'name'");
            }
        }

        return parse;
    }
}
