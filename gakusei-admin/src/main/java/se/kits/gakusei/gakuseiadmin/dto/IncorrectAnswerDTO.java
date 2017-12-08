package se.kits.gakusei.gakuseiadmin.dto;

public class IncorrectAnswerDTO {

    private Long id;
    private String incorrectAnswer;

    public IncorrectAnswerDTO(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIncorrectAnswer() {
        return incorrectAnswer;
    }

    public void setIncorrectAnswer(String incorrectAnswer) {
        this.incorrectAnswer = incorrectAnswer;
    }
}
