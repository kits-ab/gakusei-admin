package se.kits.gakusei.gakuseiadmin.unit.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.kits.gakusei.content.model.IncorrectAnswer;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.model.QuizNugget;
import se.kits.gakusei.content.repository.IncorrectAnswerRepository;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.content.repository.QuizRepository;
import se.kits.gakusei.gakuseiadmin.dto.QuizNuggetDTO;
import se.kits.gakusei.gakuseiadmin.tools.AdminTestTools;
import se.kits.gakusei.gakuseiadmin.util.AdminQuizHandler;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminQuizHandlerTest {

    @Autowired
    private AdminQuizHandler adminQuizHandler;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizNuggetRepository quizNuggetRepository;

    @Autowired
    private IncorrectAnswerRepository incorrectAnswerRepository;

    private Quiz quiz;
    private QuizNugget quizNugget;
    private List<IncorrectAnswer> incorrectAnswers;
    private QuizNuggetDTO quizNuggetDTO;

    @Before
    public void setup() {
        quiz = quizRepository.save(AdminTestTools.generateQuiz("test"));
        quizNugget = AdminTestTools.generateQuizNugget(quiz);
        incorrectAnswers = AdminTestTools.generateIncorrectAnswersWithoutNugget(3);
        quizNuggetDTO = AdminTestTools.generateQuizNuggetDTO(quiz.getId(), quizNugget, incorrectAnswers);
    }

    @After
    public void tearDown() {
        AdminTestTools.tearDownQuiz(quizRepository, quizNuggetRepository, incorrectAnswerRepository);
    }

    @Test
    public void testCreateQuizNugget() {
        QuizNugget testQuizNugget = adminQuizHandler.createQuizNugget(quizNuggetDTO);
        List<Long> quizNuggetIds = quizNuggetRepository.findByQuizId(quiz.getId()).stream().map(QuizNugget::getId)
                .collect(Collectors.toList());
        assertTrue(quizNuggetIds.contains(testQuizNugget.getId()));
    }

    @Test
    public void testCreateIncorrectAnswers() {
        QuizNugget testQuizNugget = quizNuggetRepository.save(quizNugget);
        List<IncorrectAnswer> testAnswers = adminQuizHandler.createIncorrectAnswers(quizNuggetDTO.getIncorrectAnswers(),
                testQuizNugget);
        List<Long> answerIds = incorrectAnswerRepository.findByQuizNuggetId(quizNugget.getId()).stream().map
                (IncorrectAnswer::getId).collect(Collectors.toList());
        List<Long> testAnswerIds = testAnswers.stream().map(IncorrectAnswer::getId).collect(Collectors.toList());
        assertTrue(answerIds.equals(testAnswerIds));
    }

    @Test
    public void testHandleDeleteQuizNugget() {
        QuizNugget testQuizNugget = quizNuggetRepository.save(quizNugget);
        incorrectAnswerRepository.save(AdminTestTools.generateIncorrectAnswers(quizNugget, 3));
        adminQuizHandler.handleDeleteQuizNugget(testQuizNugget.getId());
        assertFalse(quizNuggetRepository.exists(testQuizNugget.getId()));
        assertTrue(incorrectAnswerRepository.findByQuizNuggetId(testQuizNugget.getId()).isEmpty());
    }

    @Test
    public void testHandleDeleteQuiz() {
        Long quizId = quiz.getId();
        QuizNugget testQuizNugget = quizNuggetRepository.save(quizNugget);
        incorrectAnswerRepository.save(AdminTestTools.generateIncorrectAnswers(quizNugget, 3));
        adminQuizHandler.handleDeleteQuiz(quizId);
        assertFalse(quizRepository.exists(quizId));
        assertTrue(quizNuggetRepository.findByQuizId(quizId).isEmpty());
        assertTrue(incorrectAnswerRepository.findByQuizNuggetId(testQuizNugget.getId()).isEmpty());
    }
}
