package se.kits.gakusei.gakuseiadmin.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.kits.gakusei.content.repository.IncorrectAnswerRepository;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.content.repository.QuizRepository;
import se.kits.gakusei.gakuseiadmin.dto.IncorrectAnswerDTO;
import se.kits.gakusei.gakuseiadmin.dto.QuizNuggetDTO;
import se.kits.gakusei.user.model.User;
import se.kits.gakusei.content.model.*;
import se.kits.gakusei.util.QuizHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public static Quiz generateQuiz(String title) {
        return createQuiz(title, "");
    }

    private static Quiz createQuiz(String title, String suffix) {
        Quiz quiz = new Quiz();
        quiz.setName(title);
        quiz.setDescription("Test description");
        return quiz;
    }

    private static HashMap<String, Object> createQuestion(Quiz quiz, int nbrOfIncorrectAnswers) {
        HashMap<String, Object> question = new HashMap<>();
        question.put(QuizHandler.QN_QUESTION, "question");
        question.put(QuizHandler.QN_CORRECT_ANSWER, "correct");
        question.put(QuizHandler.QN_QUIZ_REF, quiz.getId());
        question.put(QuizHandler.QN_INCORRECT_ANSWERS, createIncorrectAnswers(nbrOfIncorrectAnswers));
        return question;
    }

    public static QuizNugget generateQuizNugget(Quiz quiz) {
        return createQuizNugget(quiz, "");
    }

    public static QuizNuggetDTO generateQuizNuggetDTO(Long quizId, QuizNugget quizNugget, List<IncorrectAnswer>
            incorrectAnswers) {
        QuizNuggetDTO quizNuggetDTO = new QuizNuggetDTO();
        quizNuggetDTO.setQuestion(quizNugget.getQuestion());
        quizNuggetDTO.setCorrectAnswer(quizNugget.getCorrectAnswer());
        quizNuggetDTO.setIncorrectAnswers(createIncorrectAnswerDTOs(incorrectAnswers));
        quizNuggetDTO.setQuizRef(quizId);
        return quizNuggetDTO;
    }

    private static List<IncorrectAnswerDTO> createIncorrectAnswerDTOs(List<IncorrectAnswer> incorrectAnswers) {
        return incorrectAnswers.stream().map(AdminTestTools::createIncorrectAnswerDTO).collect(Collectors.toList());
    }

    private static IncorrectAnswerDTO createIncorrectAnswerDTO(IncorrectAnswer incorrectAnswer) {
        IncorrectAnswerDTO dto = new IncorrectAnswerDTO();
        dto.setIncorrectAnswer(incorrectAnswer.getIncorrectAnswer());
        return dto;
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

    public static String generateQuestionString(Quiz quiz, int nbrOfIncorrectAnswers) throws Exception{
        List<HashMap<String, Object>> questions = new ArrayList<>();
        questions.add(createQuestion(quiz, nbrOfIncorrectAnswers));
        return new ObjectMapper().writeValueAsString(questions);
    }

    public static IncorrectAnswer generateIncorrectAnswer(QuizNugget quizNugget) {
        return createIncorrectAnswer(quizNugget, "");
    }

    public static List<IncorrectAnswer> generateIncorrectAnswers(QuizNugget quizNugget, int nbrOfIncorrectAnswers) {
        List<IncorrectAnswer> incorrectAnswers = new ArrayList<>();
         for (int i = 1; i <= nbrOfIncorrectAnswers; i++) {
            incorrectAnswers.add(createIncorrectAnswer(quizNugget, Integer.toString(i)));
        }
        return incorrectAnswers;
    }

    public static List<IncorrectAnswer> generateIncorrectAnswersWithoutNugget(int nbrOfIncorrectAnswers) {
        List<IncorrectAnswer> incorrectAnswers = new ArrayList<>();
        for (int i = 1; i <= nbrOfIncorrectAnswers; i++) {
            incorrectAnswers.add(createIncorrectAnswer(null, Integer.toString(i)));
        }
        return incorrectAnswers;
    }

    private static IncorrectAnswer createIncorrectAnswer(QuizNugget quizNugget, String suffix) {
        IncorrectAnswer answer = new IncorrectAnswer();
        answer.setIncorrectAnswer("incorrect " + suffix);
        answer.setQuizNugget(quizNugget);
        return answer;
    }

    public static List<QuizNugget> generateQuizNuggets(Quiz quiz, int nbrOfNuggets) {
        List<QuizNugget> quizNuggets = new ArrayList<>();
        for (int i = 1; i <= nbrOfNuggets; i++) {
            quizNuggets.add(createQuizNugget(quiz, Integer.toString(i)));
        }
        return quizNuggets;
    }

    public static List<QuizNugget> iterableToQuizNuggetList(Iterable<QuizNugget> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    public static void tearDownQuiz(QuizRepository qr, QuizNuggetRepository qnr, IncorrectAnswerRepository iar) {
        iar.deleteAll();
        qnr.deleteAll();
        qr.deleteAll();
    }

    public static String generateNuggetString(Nugget nugget) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(nugget);
    }

    public static Nugget generateNugget(WordType wordType, List<Book> books) {
        return createNugget(wordType, books,"");
    }

    private static Nugget createNugget(WordType wordType, List<Book> books, String suffix) {
        Nugget nugget = new Nugget();
        nugget.setDescription("description" + suffix);
        nugget.setEnglish("english" + suffix);
        nugget.setSwedish("swedish" + suffix);
        nugget.setJpRead("jpRead" + suffix);
        nugget.setJpWrite("jpWrite" + suffix);
        nugget.setWordType(wordType);
        nugget.setBooks(books);
        return nugget;
    }

    public static Lesson generateLesson(String name, List<Nugget> nuggets) {
        return createLesson(name, nuggets, "");
    }

    public static String generateLessonString(Lesson lesson) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(lesson);
    }

    private static Lesson createLesson(String name, List<Nugget> nuggets, String suffix) {
        Lesson lesson = new Lesson();
        lesson.setName(name + suffix);
        lesson.setDescription("description" + suffix);
        lesson.setNuggets(nuggets);
        return lesson;
    }
}
