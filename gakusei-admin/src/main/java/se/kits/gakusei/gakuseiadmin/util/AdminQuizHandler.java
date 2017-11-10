package se.kits.gakusei.gakuseiadmin.util;

import org.springframework.stereotype.Component;
import se.kits.gakusei.content.model.IncorrectAnswers;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.model.QuizNugget;
import se.kits.gakusei.util.QuizHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminQuizHandler extends QuizHandler {

    public HashMap<String, Object> createAndValidateQuizNugget(HashMap<String, Object> myQuizNugget) throws FormValidator.FormException {
        FormValidator quizValidator = new FormValidator(this.prepareQuizValidation(false));

        quizValidator.validate(myQuizNugget);
        this.validateIncorrectAnswers((List<?>) myQuizNugget.get(this.QN_INCORRECT_ANSWERS), false, null);
        return this.createQuizNugget(myQuizNugget);
    }

    private HashMap<String, Object>[] prepareQuizValidation(boolean onUpdate) {
        List<HashMap<String, Object>> arr_ls = new ArrayList<>();

        arr_ls.add(FormValidator.createObjInfo(this.QN_QUESTION, FormValidator.DATA_TYPE_STRING, null));
        arr_ls.add(FormValidator.createObjInfo(this.QN_CORRECT_ANSWER, FormValidator.DATA_TYPE_STRING, null));
        arr_ls.add(FormValidator.createObjInfo(this.QN_INCORRECT_ANSWERS, FormValidator.DATA_TYPE_LIST, null));
        arr_ls.add(FormValidator.createObjInfo(this.QN_QUIZ_REF, FormValidator.DATA_TYPE_INT, this.quizRepository));
        if (onUpdate)
            arr_ls.add(FormValidator.createObjInfo(this.QN_ID, FormValidator.DATA_TYPE_INT, this.quizNuggetRepository));

        HashMap<String, Object>[] hash_ls = new HashMap[arr_ls.size()];
        arr_ls.toArray(hash_ls);
        return hash_ls;
    }

    private HashMap<String, Object>[] prepareIncorrectAnswerValidation(boolean onUpdate) {
        List<HashMap<String, Object>> arr_ls = new ArrayList<>();

        arr_ls.add(FormValidator.createObjInfo(this.IA_INCORRECT_ANSWERS, FormValidator.DATA_TYPE_STRING, null));
        if (onUpdate)
            arr_ls.add(FormValidator.createObjInfo(this.IA_ID, FormValidator.DATA_TYPE_INT, this.incorrectAnswerRepository));

        HashMap<String, Object>[] hash_ls = new HashMap[arr_ls.size()];
        arr_ls.toArray(hash_ls);
        return hash_ls;
    }

    private void validateIncorrectAnswers(List<?> myIncorrectAnswers, boolean onUpdate,
                                          Long quizNuggetId) throws FormValidator.FormException {
        FormValidator incorrectAnswerValidator = new FormValidator(this.prepareIncorrectAnswerValidation(onUpdate));
        HashMap<String, Object> errors = new HashMap<>();
        if (myIncorrectAnswers.size() < 3)
            errors.put("Size", "At least 3 incorrect answers should be provided");

        int i = 0;
        for (Object myIncorrectAnswer : myIncorrectAnswers) {
            String err_key = String.format("IncorrectAnswer%d", i);
            if (!(myIncorrectAnswer instanceof HashMap<?, ?>)) {
                errors.put(err_key, "Wrong type");
                continue;
            }
            HashMap<String, Object> myIncorrectAnswer_map = (HashMap<String, Object>) myIncorrectAnswer;
            try {
                incorrectAnswerValidator.validate(myIncorrectAnswer_map);
            } catch (FormValidator.FormException exc) {
                errors.put(err_key, exc.getErrMap());
            }
            if (!errors.containsKey(err_key) && onUpdate) {
                if (!incorrectAnswerRepository.exists(new Long((int) myIncorrectAnswer_map.get(this.QN_ID)))) {
                    errors.put(err_key, "Does not exist");
                }
            }
            i++;
        }
        if (!errors.isEmpty())
            throw new FormValidator.FormException(errors);
    }

    private HashMap<String, Object> createQuizNugget(HashMap<String, Object> myQuizNugget) {
        QuizNugget quizNugget = new QuizNugget();
        quizNugget.setQuestion((String) myQuizNugget.get(this.QN_QUESTION));
        quizNugget.setCorrectAnswer((String) myQuizNugget.get(this.QN_CORRECT_ANSWER));
        quizNugget.setQuiz(this.quizRepository.findOne(new Long((Integer) myQuizNugget.get(this.QN_QUIZ_REF))));
        HashMap<String, Object> newMyQuizNugget = this.convertQuizNugget(this.quizNuggetRepository.save(quizNugget));

        for (Object myIncorrectAnswer : (List) myQuizNugget.get(this.QN_INCORRECT_ANSWERS)) {
            this.createIncorrectAnswer((HashMap) myIncorrectAnswer, quizNugget);
        }
        return newMyQuizNugget;
    }

    private void createIncorrectAnswer(HashMap<String, Object> myIncorrectAnswer, QuizNugget quizNugget) {
        IncorrectAnswers incorrectAnswer = new IncorrectAnswers();
        incorrectAnswer.setIncorrectAnswer((String) myIncorrectAnswer.get(this.IA_INCORRECT_ANSWERS));
        incorrectAnswer.setQuizNugget(quizNugget);
        this.incorrectAnswerRepository.save(incorrectAnswer);
    }

    public void updateAndValidateQuizNugget(HashMap<String, Object> myQuizNugget) throws FormValidator.FormException {
        FormValidator quizValidator = new FormValidator(this.prepareQuizValidation(true));
        quizValidator.validate(myQuizNugget);
        this.validateIncorrectAnswers((List<?>) myQuizNugget.get(this.QN_INCORRECT_ANSWERS), true,
                new Long((int) myQuizNugget.get(this.QN_ID)));

        this.updateQuizNugget(myQuizNugget);

        int i = 0;
        for (Object myIncorrectAnswer : (List) myQuizNugget.get(this.QN_INCORRECT_ANSWERS)) {
            this.updateIncorrectAnswer((HashMap<String, Object>) myIncorrectAnswer);
            i++;
        }
    }

    private void updateQuizNugget(HashMap<String, Object> myQuizNugget) {
        QuizNugget quizNugget = this.quizNuggetRepository.findOne(new Long((int) myQuizNugget.get(this.QN_ID)));
        quizNugget.setQuestion((String) myQuizNugget.get(this.QN_QUESTION));
        quizNugget.setCorrectAnswer((String) myQuizNugget.get(this.QN_CORRECT_ANSWER));

        Quiz quizRef = this.quizRepository.findOne(new Long((int) myQuizNugget.get(this.QN_QUIZ_REF)));
        quizNugget.setQuiz(quizRef);

        this.quizNuggetRepository.save(quizNugget);
    }

    private void updateIncorrectAnswer(HashMap<String, Object> myIncorrectAnswer) {
        IncorrectAnswers incorrectAnswer = this.incorrectAnswerRepository.findOne(
                new Long((int) myIncorrectAnswer.get(this.IA_ID)));
        incorrectAnswer.setIncorrectAnswer((String) myIncorrectAnswer.get(this.IA_INCORRECT_ANSWERS));

        this.incorrectAnswerRepository.save(incorrectAnswer);
    }


    public Quiz createNewQuiz(String name, String description){
        Quiz toReturn = new Quiz();
        toReturn.setName(name);
        toReturn.setDescription(description);

        return toReturn;
    }

    public void saveQuiz(Quiz quiz,
                          ArrayList<QuizNugget> quizNuggets,
                          ArrayList<Iterable<IncorrectAnswers>> incorrectAnswers) {
        quizRepository.save(quiz);
        quizNuggetRepository.save(quizNuggets);
        for (Iterable<IncorrectAnswers> ia : incorrectAnswers) {
            incorrectAnswerRepository.save(ia);
        }

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
}
