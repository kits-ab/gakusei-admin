package se.kits.gakusei.gakuseiadmin.utility;

import com.univocity.parsers.common.processor.ObjectRowListProcessor;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.content.repository.NuggetRepository;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CSVToDatabase {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private NuggetRepository nuggetRepository;

    MultipartFile file;

    private final int ID_INDEX = 0;
    private final int DESCRIPTION_INDEX = 1;
    private final int TITLE_INDEX = 2;
    private final int TYPE_INDEX = 3;
    private final int LESSONS_INDEX = 4;
    private final int SWEDISH_INDEX = 5;
    private final int ENGLISH_INDEX = 6;
    private final int JP_READ_INDEX = 7;
    private final int JP_WRITE_INDEX = 8;
    private final int HIDDEN_INDEX = 9;

    public CSVToDatabase(MultipartFile f){
        file = f;
    }

    public boolean parse() {

        CsvParserSettings parserSettings = new CsvParserSettings();
        RowListProcessor rowProcessor = new RowListProcessor();

        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);

        try {
            parser.parse(file.getInputStream());
            String[] headers = rowProcessor.getHeaders();
            List<String[]> rows = rowProcessor.getRows();

            createNuggets(rows);

            return true;

        } catch (IOException e) {
            logger.error(e.toString());
            return false;
        }

    }

    private boolean createNuggets(List<String[]> rows){

        for(String[] csvNugget : rows){
            Nugget nugget = new Nugget();
            nugget.setId(csvNugget[ID_INDEX]);
            nugget.setDescription(csvNugget[DESCRIPTION_INDEX]);
            nugget.setEnglish(csvNugget[ENGLISH_INDEX]);
            nugget.setSwedish(csvNugget[SWEDISH_INDEX]);
            nugget.setJpRead(csvNugget[JP_READ_INDEX]);
            nugget.setJpWrite(csvNugget[JP_WRITE_INDEX]);
            nugget.setWordType(createWordType(csvNugget[TYPE_INDEX]));
            nugget.setHidden(createBoolean(csvNugget[HIDDEN_INDEX]));
            nugget.setLessons(createLessonList(csvNugget[LESSONS_INDEX]));
            nugget.setTitle(createBookTitle(csvNugget[TITLE_INDEX]));

            nuggetRepository.save(nugget);
        }

        return true;
    }

    private WordType createWordType(String s){
        WordType newWordType = new WordType();
        newWordType.setType(s);
        return newWordType;
    }

    private boolean createBoolean(String s){
        return Boolean.parseBoolean(s);
    }

    private List<Lesson> createLessonList(String s){

        List<Lesson> lessons = new ArrayList<>();
        // List of lessons is comma-separated
        String[] listOfStringLessons = s.split(",");

        System.out.println(Arrays.toString(listOfStringLessons));

        for(String stringLesson : listOfStringLessons){
            String trimmed = stringLesson.trim();
            Lesson l = lessonRepository.findByName(trimmed);
            lessons.add(l);
        }

        return lessons;
    }

    private Book createBookTitle(String s){
        Book newBook = new Book();
        newBook.setTitle(s);
        return newBook;
    }

}
