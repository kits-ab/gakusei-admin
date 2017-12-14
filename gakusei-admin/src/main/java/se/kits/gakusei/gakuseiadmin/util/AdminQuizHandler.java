package se.kits.gakusei.gakuseiadmin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kits.gakusei.content.model.IncorrectAnswer;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.model.QuizNugget;
import se.kits.gakusei.content.repository.IncorrectAnswerRepository;
import se.kits.gakusei.content.repository.QuizNuggetRepository;
import se.kits.gakusei.content.repository.QuizRepository;
import se.kits.gakusei.gakuseiadmin.dto.IncorrectAnswerDTO;
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
        return updateQuizNugget(quizNuggetDTO, new QuizNugget());
    }

    public List<IncorrectAnswer> createIncorrectAnswers(List<IncorrectAnswerDTO> incorrectAnswerDTOs,
                                                        QuizNugget quizNugget) {
        List<IncorrectAnswer> incorrectAnswers = incorrectAnswerDTOs.stream().map(a -> createIncorrectAnswer(a,
                quizNugget)).collect(Collectors.toList());
        incorrectAnswerRepository.save(incorrectAnswers);
        return incorrectAnswers;
    }

    private IncorrectAnswer createIncorrectAnswer(IncorrectAnswerDTO incorrectAnswerDTO, QuizNugget quizNugget) {
        IncorrectAnswer incorrectAnswer = new IncorrectAnswer();
        incorrectAnswer.setIncorrectAnswer(incorrectAnswerDTO.getIncorrectAnswer());
        incorrectAnswer.setQuizNugget(quizNugget);
        return incorrectAnswer;
    }

    public QuizNuggetDTO patchQuizNuggetDTO(QuizNuggetDTO quizNuggetDTO, List<IncorrectAnswerDTO> incorrectAnswers,
                                             Long quizNuggetId) {
        quizNuggetDTO.setIncorrectAnswers(incorrectAnswers);
        quizNuggetDTO.setId(quizNuggetId);
        return quizNuggetDTO;
    }

    public List<IncorrectAnswerDTO> createIncorrectAnswerDTOs(List<IncorrectAnswer> incorrectAnswers) {
        return incorrectAnswers.stream().map(AdminQuizHandler::createIncorrectAnswerDTO)
                .collect(Collectors.toList());
    }

    private static IncorrectAnswerDTO createIncorrectAnswerDTO(IncorrectAnswer incorrectAnswer) {
        IncorrectAnswerDTO incorrectAnswerDTO = new IncorrectAnswerDTO();
        incorrectAnswerDTO.setIncorrectAnswer(incorrectAnswer.getIncorrectAnswer());
        incorrectAnswerDTO.setId(incorrectAnswer.getId());
        return incorrectAnswerDTO;

    }

    public QuizNugget updateQuizNugget(QuizNuggetDTO quizNuggetDTO, QuizNugget quizNugget) {
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
            dto.setId(quizNugget.getId());
            dto.setQuizRef(quizId);
            dto.setIncorrectAnswers(createIncorrectAnswerDTOs(incorrectAnswerRepository.findByQuizNuggetId
                    (quizNugget.getId())));
            dto.setCorrectAnswer(quizNugget.getCorrectAnswer());
            dto.setQuestion(quizNugget.getQuestion());
            quizNuggetDTOs.add(dto);
        }
        return quizNuggetDTOs;
    }
}
