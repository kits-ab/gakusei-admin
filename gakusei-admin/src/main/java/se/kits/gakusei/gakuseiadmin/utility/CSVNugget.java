package se.kits.gakusei.gakuseiadmin.utility;

import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.content.repository.WordTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVNugget {

    private LessonRepository lessonRepository;
    private BookRepository bookRepository;
    private WordTypeRepository wordTypeRepository;

    private String[] values;

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

    public CSVNugget(String[] values, LessonRepository lr, BookRepository br, WordTypeRepository wtr){
        this.values = values;
        lessonRepository = lr;
        bookRepository = br;
        wordTypeRepository = wtr;
    }

    public Nugget getNugget(){

        Nugget nugget = new Nugget();

        nugget.setId(values[ID_INDEX]);
        nugget.setDescription(values[DESCRIPTION_INDEX]);
        nugget.setEnglish(values[ENGLISH_INDEX]);
        nugget.setSwedish(values[SWEDISH_INDEX]);
        nugget.setJpRead(values[JP_READ_INDEX]);
        nugget.setJpWrite(values[JP_WRITE_INDEX]);
        nugget.setWordType(createWordType(values[TYPE_INDEX]));
        nugget.setHidden(createBoolean(values[HIDDEN_INDEX]));
        nugget.setLessons(createLessonList(values[LESSONS_INDEX]));
        nugget.setTitle(createBookTitle(values[TITLE_INDEX]));

        return nugget;
    }

    private WordType createWordType(String s){
        return new WordType();
        // return wordTypeRepository.findByType(s); TODO : Make sure this works with repository/database
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
            // Lesson l = lessonRepository.findByName(trimmed); TODO : Make sure this works with repository/database
            lessons.add(new Lesson());
        }

        return lessons;
    }

    private Book createBookTitle(String s){
        return new Book();
        // return bookRepository.findByTitle(s); TODO : Make sure this works with repository/database
    }
}
