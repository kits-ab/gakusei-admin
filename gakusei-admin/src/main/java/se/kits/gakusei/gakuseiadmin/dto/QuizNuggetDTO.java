package se.kits.gakusei.gakuseiadmin.dto;

import se.kits.gakusei.content.model.IncorrectAnswer;

import java.util.List;

public class QuizNuggetDTO {

    private Long id;
    private Long quizRef;
    private String question;
    private String correctAnswer;
    private List<IncorrectAnswer> incorrectAnswers;

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

    public List<IncorrectAnswer> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<IncorrectAnswer> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
