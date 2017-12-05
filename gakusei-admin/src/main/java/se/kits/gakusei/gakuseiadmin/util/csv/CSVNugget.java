package se.kits.gakusei.gakuseiadmin.util.csv;

import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.content.repository.WordTypeRepository;
import se.kits.gakusei.util.ParserFailureException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVNugget {

    private LessonRepository lessonRepository;
    private BookRepository bookRepository;
    private WordTypeRepository wordTypeRepository;

    private String[] values;

    private final int DESCRIPTION_INDEX = 0;
    private final int TITLE_INDEX = 1;
    private final int TYPE_INDEX = 2;
    private final int LESSONS_INDEX = 3;
    private final int SWEDISH_INDEX = 4;
    private final int ENGLISH_INDEX = 5;
    private final int JP_READ_INDEX = 6;
    private final int JP_WRITE_INDEX = 7;
    private final int HIDDEN_INDEX = 8;

    private final int EXPECTED_NUMBER_OF_VALUES = 9;

    public CSVNugget(String[] values, LessonRepository lr, BookRepository br, WordTypeRepository wtr){
        this.values = values;
        lessonRepository = lr;
        bookRepository = br;
        wordTypeRepository = wtr;
        initialCheck();
    }

    private void initialCheck(){
        if(values.length != EXPECTED_NUMBER_OF_VALUES){
            throw new ParserFailureException("Unexpected number of values in row: " + Arrays.toString(values)
                                            + "\nExpected " + EXPECTED_NUMBER_OF_VALUES + " but got " + values.length);
        }
    }

    public Nugget getNugget(){

        Nugget nugget = new Nugget();

        nugget.setDescription(values[DESCRIPTION_INDEX]);
        nugget.setEnglish(values[ENGLISH_INDEX]);
        nugget.setSwedish(values[SWEDISH_INDEX]);
        nugget.setJpRead(values[JP_READ_INDEX]);
        nugget.setJpWrite(values[JP_WRITE_INDEX]);
        nugget.setWordType(createWordType(values[TYPE_INDEX]));
        nugget.setHidden(createBoolean(values[HIDDEN_INDEX]));
        nugget.setLessons(createLessonList(values[LESSONS_INDEX]));
        nugget.setBooks(createBookList(values[TITLE_INDEX]));

        return nugget;
    }

    private WordType createWordType(String s){
        WordType wt = wordTypeRepository.findByType(s);

        if(wt != null) {
            return wt;
        } else {
            throw new ParserFailureException("Invalid word type \"" + s + "\" in row: " + Arrays.toString(values)
                                            + "\nWord type does not exist in database");
        }
    }

    private boolean createBoolean(String s){
        return Boolean.parseBoolean(s);
    }

    private List<Lesson> createLessonList(String s){

        List<Lesson> lessons = new ArrayList<>();
        // List of lessons is comma-separated
        String[] listOfStringLessons = s.split(",");

        for(String stringLesson : listOfStringLessons){
            String trimmed = stringLesson.trim();
            Lesson l = lessonRepository.findByName(trimmed);
            if(l != null) {
                lessons.add(l);
            } else {
                throw new ParserFailureException("Invalid lesson \"" + trimmed + "\" in row: " + Arrays.toString(values)
                                                + "\nLesson does not exist in database");
            }
        }

        return lessons;
    }

    private List<Book> createBookList(String s){
        String[] listOfTitles = s.split(",");
        return Arrays.stream(listOfTitles)
                .map(title -> getBook(title.trim())).collect(Collectors.toList());
    }

    private Book getBook(String title) {
        Book book = bookRepository.findByTitle(title);
        if (book != null) {
            return book;
        }

        throw new ParserFailureException("Invalid book \"" + title + "\" in row: " + Arrays.toString(values)
                    + "\nBook does not exist in database");
    }
}
