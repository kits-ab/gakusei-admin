package se.kits.gakusei.gakuseiadmin.dto;

import java.util.List;

public class QuizNuggetDTO {

    private Long id;
    private Long quizRef;
    private String question;
    private String correctAnswer;
    private List<IncorrectAnswerDTO> incorrectAnswerDTOs;

    public QuizNuggetDTO(){}


    public Long getQuizRef() {
        return quizRef;
    }

    public void setQuizRef(Long quizRef) {
        this.quizRef = quizRef;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<IncorrectAnswerDTO> getIncorrectAnswers() {
        return incorrectAnswerDTOs;
    }

    public void setIncorrectAnswers(List<IncorrectAnswerDTO> incorrectAnswerDTOs) {
        this.incorrectAnswerDTOs = incorrectAnswerDTOs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
