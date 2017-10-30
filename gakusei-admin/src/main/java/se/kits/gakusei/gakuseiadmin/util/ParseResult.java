package se.kits.gakusei.gakuseiadmin.util;

import se.kits.gakusei.content.model.IncorrectAnswers;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.model.QuizNugget;
import se.kits.gakusei.util.ParserFailureException;
import se.kits.gakusei.util.csv.CSVQuizNugget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParseResult {

    private final int QUIZNAME_INDEX = 0;
    private final int QUIZDESCRIPTION_INDEX = 1;

    private final String HEADERS_KEY = "HEADERS";
    private final String ROWS_KEY = "ROWS";

    private List<String[]> headers;
    private List<String[]> rows;

    private ArrayList<String> errors;

    private boolean successful;

    private Quiz associatedQuiz;

    private ArrayList<QuizNugget> parsedQuizNuggets;
    private ArrayList<Iterable<IncorrectAnswers>> parsedIncorrectAnswers;

    public ParseResult(Map<String, List<String[]>> parseResult, Quiz associatedQuiz){
        parsedIncorrectAnswers = new ArrayList<>();
        parsedQuizNuggets = new ArrayList<>();
        errors = new ArrayList<>();

        successful = true;

        this.associatedQuiz = associatedQuiz;
        headers = parseResult.get(HEADERS_KEY);
        rows = parseResult.get(ROWS_KEY);

        extractRowInformation();
    }

    private void extractRowInformation(){
        for(String[] row : rows){
            try {
                String[] rowWithQuizInfo = appendQuizInfo(row);

                CSVQuizNugget csvQuizNugget = new CSVQuizNugget(rowWithQuizInfo);
                QuizNugget quizNugget = csvQuizNugget.getQuizNugget(associatedQuiz);
                Iterable<IncorrectAnswers> incorrectAnswers = csvQuizNugget.getIncorrectAnswers(quizNugget);

                parsedQuizNuggets.add(quizNugget);
                parsedIncorrectAnswers.add(incorrectAnswers);
            } catch(ParserFailureException pfe){
                successful = false;
                errors.add(pfe.getMessage());
            }
        }
    }

    private String[] appendQuizInfo(String[] quizNuggetRow){
        String[] appendedRow = new String[2 + quizNuggetRow.length];

        appendedRow[QUIZNAME_INDEX] = associatedQuiz.getName();
        appendedRow[QUIZDESCRIPTION_INDEX] = associatedQuiz.getDescription();

        for(int i = 0; i < quizNuggetRow.length; i++){
            appendedRow[i + 2] = quizNuggetRow[i];
        }

        return appendedRow;
    }

    public List<String> getErrors() {
        return errors;
    }

    public ArrayList<QuizNugget> getParsedQuizNuggets() {
        return parsedQuizNuggets;
    }

    public ArrayList<Iterable<IncorrectAnswers>> getParsedIncorrectAnswers() {
        return parsedIncorrectAnswers;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
