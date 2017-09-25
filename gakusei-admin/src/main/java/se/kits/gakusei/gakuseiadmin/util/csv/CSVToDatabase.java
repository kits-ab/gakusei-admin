package se.kits.gakusei.gakuseiadmin.util.csv;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.gakuseiadmin.content.AdminWordTypeRepository;
import se.kits.gakusei.gakuseiadmin.util.ParserFailureException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVToDatabase {

    private LessonRepository lessonRepository;

    private BookRepository bookRepository;

    private AdminWordTypeRepository adminWordTypeRepository;

    private MultipartFile file;

    public CSVToDatabase(MultipartFile f, LessonRepository lr, AdminWordTypeRepository wr, BookRepository br){
        file = f;
        lessonRepository = lr;
        bookRepository = br;
        adminWordTypeRepository = wr;
    }

    public List<Nugget> parse(){

        RowListProcessor rowProcessor = new RowListProcessor();
        CsvParser parser = setupParser(rowProcessor);

        try {
            parser.parse(file.getInputStream());
            String[] headers = rowProcessor.getHeaders(); // TODO : Do something productive with these? Validation?
            List<String[]> rows = rowProcessor.getRows();

            return createNuggets(rows);

        } catch (IOException e) {
            throw new ParserFailureException("Could not open file: "  + e.getMessage());
        }

    }

    private List<Nugget> createNuggets(List<String[]> rows){

        List<Nugget> nuggets = new ArrayList<>();

        for(String[] stringList : rows){
            CSVNugget csvNugget = new CSVNugget(stringList, lessonRepository, bookRepository, adminWordTypeRepository);
            nuggets.add(csvNugget.getNugget());
        }

        return nuggets;
    }

    private CsvParser setupParser(RowListProcessor rowProcessor){
        CsvParserSettings parserSettings = new CsvParserSettings();

        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        return new CsvParser(parserSettings);
    }



}
