package se.kits.gakusei.gakuseiadmin.tools;

import se.kits.gakusei.content.model.Course;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.user.model.User;

import java.util.ArrayList;

public class AdminTestTools {

    public static Iterable<WordType> generateWordTypes() {
        Iterable<WordType> wordTypes = new ArrayList<WordType>();
        for (int i = 0; i < 5; i++) {
            WordType wordType = new WordType();
            wordType.setType("type" + i);
        }

        return wordTypes;
    }

    public static Course createCourse() {
        Course course = new Course();
        course.setName("Test course");
        return course;
    }

    public static User createUser() {
        User user = new User();
        user.setRole("ROLE_ADMIN");
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEvents(null);
        user.setProgressTrackingList(null);
        user.setUsersLessons(null);

        return user;
    }

    public static Book generateTestBook(String title) {
        Book book = new Book();
        book.setTitle(title);
        return book;
    }

    public static Book updateTestBook(Book toBeUpdated, String newTitle) {
        Book book = toBeUpdated;
        book.setTitle(newTitle);
        return book;
    }

    public static WordType generateTestWordType(String type) {
        WordType wordType = new WordType();
        wordType.setType(type);
        return wordType;
    }

    public static WordType updateTestWordType(WordType toBeUpdated, String newType) {
        WordType wordType = toBeUpdated;
        wordType.setType(newType);
        return wordType;
    }
}
