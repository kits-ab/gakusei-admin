package se.kits.gakusei.gakuseiadmin.tools;

import se.kits.gakusei.content.repository.IncorrectAnswerRepository;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.content.repository.QuizRepository;
import se.kits.gakusei.user.model.User;
import se.kits.gakusei.content.model.*;
import se.kits.gakusei.util.QuizHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public static User createUser(String username, String role) {
        User user = new User();
        user.setRole(role);
        user.setUsername(username);
        user.setPassword("password");
        user.setEvents(null);
        user.setProgressTrackingList(null);
        user.setUsersLessons(null);

        return user;
    }

    public static ArrayList<User> createUsers() {
        ArrayList<User> toReturn = new ArrayList<>();

        toReturn.add(createUser("admin", "ROLE_ADMIN"));
        toReturn.add(createUser("user", "ROLE_USER"));
        toReturn.add(createUser("owner", "ROLE_ADMIN"));
        toReturn.add(createUser("Alice", "ROLE_USER"));
        toReturn.add(createUser("Bob", "ROLE_USER"));

        return toReturn;
    }

    public static Iterable<User> matchingUsernameAndRole(Iterable<User> toTest, String name, String role) {
        ArrayList<User> toReturn = new ArrayList<>();
        for(User u : toTest) {
            if (u.getUsername().toUpperCase().contains(name.toUpperCase())
                    && u.getRole().toUpperCase().contains(role.toUpperCase())) {
                toReturn.add(u);
            }
        }
        return toReturn;
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

    public static HashMap<String, Object> createQuestion(Quiz quiz, int nbrOfIncorrectAnswers) {
        HashMap<String, Object> question = new HashMap<>();
        question.put(QuizHandler.QN_QUESTION, "question");
        question.put(QuizHandler.QN_CORRECT_ANSWER, "correct");
        question.put(QuizHandler.QN_QUIZ_REF, quiz.getId());
        question.put(QuizHandler.QN_INCORRECT_ANSWERS, createIncorrectAnswers(nbrOfIncorrectAnswers));
        return question;
    }

    private static QuizNugget createQuizNugget(Quiz quiz, String suffix) {
        QuizNugget nugget = new QuizNugget();
        nugget.setQuiz(quiz);
        nugget.setQuestion("Question " + suffix);
        nugget.setCorrectAnswer("correct alternative");
        return nugget;
    }

    private static List<HashMap<String, Object>> createIncorrectAnswers(int nbrOfIncorrectAnswers) {
        List<HashMap<String, Object>> incorrectAnswers = new ArrayList<>();
        for (int i = 1; i <= nbrOfIncorrectAnswers; i++) {
            HashMap<String, Object> incorrectAnswer = new HashMap<>();
            incorrectAnswer.put(QuizHandler.IA_INCORRECT_ANSWERS, "incorrect alternative " + Integer.toString(i));
            incorrectAnswers.add(incorrectAnswer);
        }
        return incorrectAnswers;
    }

    public static void tearDownQuiz(QuizRepository qr, QuizNuggetRepository qnr, IncorrectAnswerRepository iar) {
        iar.deleteAll();
        qnr.deleteAll();
        qr.deleteAll();
    }
}
