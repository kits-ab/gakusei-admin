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
import static org.junit.Assert.assertEquals;

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

    private IncorrectAnswers generateIncorrectAnswer() {
        IncorrectAnswers incorrectAnswers = new IncorrectAnswers();
        incorrectAnswers.setIncorrectAnswer("Incorrect");
        QuizNugget quizNugget = this.generateQuizNugget(true);
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
        quizNugget = quizAdminHandler.createAndValidateQuizNugget(this.constructQuizNugget());
        try {
            quizNugget = quizAdminHandler.createAndValidateQuizNugget(this.constructQuizNugget());
        } catch (Exception exc) {
            assert false;
        }

        assertTrue(quizNugget.containsKey("id"));
        assertTrue(this.quizNuggetRepository.exists((Long) quizNugget.get("id")));
    }
}
