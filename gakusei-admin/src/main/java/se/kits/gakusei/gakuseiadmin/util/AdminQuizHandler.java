package se.kits.gakusei.gakuseiadmin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kits.gakusei.content.model.IncorrectAnswer;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.model.QuizNugget;
import se.kits.gakusei.content.repository.IncorrectAnswerRepository;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.content.repository.QuizRepository;
import se.kits.gakusei.gakuseiadmin.dto.QuizNuggetDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminQuizHandler {

    private QuizRepository quizRepository;
    private QuizNuggetRepository quizNuggetRepository;
    private IncorrectAnswerRepository incorrectAnswerRepository;

    @Autowired
    public AdminQuizHandler(QuizRepository qr, QuizNuggetRepository qnr, IncorrectAnswerRepository iar) {
        quizRepository = qr;
        quizNuggetRepository = qnr;
        incorrectAnswerRepository = iar;
    }

    public QuizNugget createQuizNugget(QuizNuggetDTO quizNuggetDTO) {
        QuizNugget quizNugget = new QuizNugget();
        quizNugget.setQuiz(quizRepository.findOne(quizNuggetDTO.getQuizRef()));
        quizNugget.setQuestion(quizNuggetDTO.getQuestion());
        quizNugget.setCorrectAnswer(quizNuggetDTO.getCorrectAnswer());
        return quizNuggetRepository.save(quizNugget);
    }

    public List<IncorrectAnswer> createIncorrectAnswers(List<IncorrectAnswer> incorrectAnswers, QuizNugget quizNugget) {
        incorrectAnswers.forEach(a -> a.setQuizNugget(quizNugget));
        incorrectAnswerRepository.save(incorrectAnswers);
        return incorrectAnswers;
    }

    public QuizNuggetDTO updateQuizNuggetDTO(QuizNuggetDTO quizNuggetDTO, List<IncorrectAnswer> incorrectAnswers,
                                             Long quizNuggetId) {
        quizNuggetDTO.setIncorrectAnswers(incorrectAnswers);
        quizNuggetDTO.setId(quizNuggetId);
        return quizNuggetDTO;
    }

    public QuizNugget updateQuizNugget(QuizNuggetDTO quizNuggetDTO) {
        QuizNugget quizNugget = quizNuggetRepository.findOne(quizNuggetDTO.getId());
        quizNugget.setQuestion(quizNuggetDTO.getQuestion());
        quizNugget.setCorrectAnswer(quizNuggetDTO.getCorrectAnswer());
        Quiz quizRef = quizRepository.findOne(quizNuggetDTO.getQuizRef());
        quizNugget.setQuiz(quizRef);
        return quizNuggetRepository.save(quizNugget);
    }


    public Quiz createQuiz(String name, String description){
        Quiz quiz = new Quiz();
        quiz.setName(name);
        quiz.setDescription(description);
        return quiz;
    }

    public void saveQuiz(Quiz quiz, List<QuizNugget> quizNuggets, List<Iterable<IncorrectAnswer>> incorrectAnswers) {
        quizRepository.save(quiz);
        quizNuggetRepository.save(quizNuggets);
        incorrectAnswers.forEach(ia -> incorrectAnswerRepository.save(ia));
    }

    public void handleDeleteQuiz(Long quizId) {
        List<Long> quizNuggetIds = quizNuggetRepository.findByQuizId(quizId).stream().map(QuizNugget::getId).collect
                (Collectors.toList());
        incorrectAnswerRepository.deleteByQuizNuggetIdIn(quizNuggetIds);
        quizNuggetRepository.deleteByQuizId(quizId);
        quizRepository.delete(quizId);
    }

    public void handleDeleteQuizNugget(Long quizNuggetId) {
        incorrectAnswerRepository.deleteByQuizNuggetId(quizNuggetId);
        quizNuggetRepository.delete(quizNuggetId);
    }

    public List<QuizNuggetDTO> getQuizNuggets(Long quizId) {
        List<QuizNugget> quizNuggets = quizNuggetRepository.findByQuizId(quizId);
        List<QuizNuggetDTO> quizNuggetDTOs = new ArrayList<>();
        for (QuizNugget quizNugget : quizNuggets) {
            QuizNuggetDTO dto = new QuizNuggetDTO();
            dto.setId(quizId);
            dto.setQuizRef(quizNugget.getQuiz().getId());
            dto.setIncorrectAnswers(incorrectAnswerRepository.findByQuizNuggetId(quizId));
            dto.setCorrectAnswer(quizNugget.getCorrectAnswer());
            dto.setQuestion(quizNugget.getQuestion());
            quizNuggetDTOs.add(dto);
        }
        return quizNuggetDTOs;
    }
}
