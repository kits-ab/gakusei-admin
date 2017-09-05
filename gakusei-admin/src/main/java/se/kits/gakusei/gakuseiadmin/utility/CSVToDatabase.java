package se.kits.gakusei.gakuseiadmin.utility;

import org.springframework.web.multipart.MultipartFile;

public class CSVToDatabase {

    MultipartFile file;

    public CSVToDatabase(MultipartFile f){
        file = f;
    }

}
