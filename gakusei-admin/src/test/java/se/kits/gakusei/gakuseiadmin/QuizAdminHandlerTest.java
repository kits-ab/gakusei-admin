package se.kits.gakusei.util;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.kits.gakusei.content.model.IncorrectAnswers;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.model.QuizNugget;
import se.kits.gakusei.content.repository.IncorrectAnswerRepository;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.content.repository.QuizRepository;
import se.kits.gakusei.gakuseiadmin.Util.QuizAdminHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuizAdminHandlerTest {

    @Autowired
    private QuizAdminHandler quizAdminHandler;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizNuggetRepository quizNuggetRepository;

    @Autowired
    private IncorrectAnswerRepository incorrectAnswerRepository;

    private Quiz generateQuiz() {
        Quiz quiz = new Quiz();
        quiz.setName("Temp quiz");
        quiz.setDescription("Temp quiz");
        return this.quizRepository.save(quiz);
    }

    private QuizNugget generateQuizNugget(boolean save) {
        QuizNugget quizNugget = new QuizNugget();
        quizNugget.setQuestion("?");
        quizNugget.setCorrectAnswer("42");
        Quiz quizRef = generateQuiz();
        quizNugget.setQuiz(quizRef);

        if (save)
            quizNugget = this.quizNuggetRepository.save(quizNugget);
        return quizNugget;
    }

    private IncorrectAnswers generateIncorrectAnswer(QuizNugget quizNugget) {
        IncorrectAnswers incorrectAnswers = new IncorrectAnswers();
        incorrectAnswers.setIncorrectAnswer("Incorrect");
        incorrectAnswers.setQuizNugget(quizNugget);
        incorrectAnswers = this.incorrectAnswerRepository.save(incorrectAnswers);
        return incorrectAnswers;
    }

    private HashMap<String, Object> constructQuizNugget() {
        HashMap<String, Object> quizNugget = this.quizAdminHandler.convertQuizNugget(this.generateQuizNugget(false));
        // NEEDS TO BE AN INT
        quizNugget.put(this.quizAdminHandler.QN_QUIZ_REF, ((Long) quizNugget.get(this.quizAdminHandler.QN_QUIZ_REF)).intValue());

        List<HashMap<String, Object>> incorrectAnswers = new ArrayList<>();
        for (int i=0; i<3; i++) {
            HashMap<String, Object> incorrectAnswer = new HashMap<>();
            incorrectAnswer.put("incorrectAnswer", "answer"+i);
            incorrectAnswers.add(incorrectAnswer);
        }
        quizNugget.put("incorrectAnswers", incorrectAnswers);

        return quizNugget;
    }

    @Test
    public void testCreateQuizNugget() throws Exception {
        HashMap<String, Object> quizNugget = null;

        try {
            quizNugget = quizAdminHandler.createAndValidateQuizNugget(this.constructQuizNugget());
        } catch (Exception exc) {
            assert false;
        }

        assertTrue(quizNugget.containsKey("id"));
        assertTrue(this.quizNuggetRepository.exists((Long) quizNugget.get("id")));
    }

    @Test
    public void testUpdateQuizNugget() throws Exception {
        QuizNugget quizNugget = this.generateQuizNugget(true);
        for (int i=0; i<3; i++)
            this.generateIncorrectAnswer(quizNugget);

        HashMap<String, Object> myQuizNugget = quizAdminHandler.convertQuizNugget(quizNugget);
        myQuizNugget.put(quizAdminHandler.QN_CORRECT_ANSWER, "Gakusei");

        // CONVERT LONG TO INT
        myQuizNugget.put(quizAdminHandler.QN_ID,((Long) myQuizNugget.get(quizAdminHandler.QN_ID)).intValue());
        myQuizNugget.put(quizAdminHandler.QN_QUIZ_REF,((Long) myQuizNugget.get(quizAdminHandler.QN_QUIZ_REF)).intValue());
        for (HashMap<String, Object> myIncorrectAnswer : (List<HashMap>) myQuizNugget.get(quizAdminHandler.QN_INCORRECT_ANSWERS))
            myIncorrectAnswer.put(quizAdminHandler.IA_ID,((Long) myIncorrectAnswer.get(quizAdminHandler.IA_ID)).intValue());

        try {
            quizAdminHandler.updateAndValidateQuizNugget(myQuizNugget);
        } catch (Exception exc) {
            assert false;
        }
    }
}
