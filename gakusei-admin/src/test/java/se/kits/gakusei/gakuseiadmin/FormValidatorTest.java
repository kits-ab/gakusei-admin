package se.kits.gakusei.gakuseiadmin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.kits.gakusei.content.model.Quiz;
import se.kits.gakusei.content.repository.QuizRepository;
import se.kits.gakusei.gakuseiadmin.Util.FormValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FormValidatorTest {

    @Autowired
    QuizRepository quizRepository;

    private HashMap<String, Object>[] set_up() {
        List<HashMap<String, Object>> arr_ls = new ArrayList<>();
        arr_ls.add(FormValidator.createObjInfo("id", FormValidator.DATA_TYPE_INT, null));
        arr_ls.add(FormValidator.createObjInfo("name", FormValidator.DATA_TYPE_STRING, null));
        arr_ls.add(FormValidator.createObjInfo("list", FormValidator.DATA_TYPE_LIST, null));
        arr_ls.add(FormValidator.createObjInfo("ref", FormValidator.DATA_TYPE_INT, this.quizRepository));

        HashMap<String, Object>[] hash_ls = new HashMap[arr_ls.size()];
        arr_ls.toArray(hash_ls);
        return hash_ls;
    }

    private boolean checkvalidate(FormValidator formValidator, HashMap<String, Object> obj) {
        boolean success = true;
        try {
            formValidator.validate(obj);
        } catch (Exception exc) {
            success = false;
        }
        return success;
    }

    @Test
    public void testValidate() throws Exception {
        FormValidator formValidator = new FormValidator(this.set_up());
        HashMap<String, Object> obj = new HashMap<>();

        assertEquals(false, this.checkvalidate(formValidator, obj));

        obj.put("id", 42);
        assertEquals(false, this.checkvalidate(formValidator, obj));

        obj.put("name", "Gakusei");
        assertEquals(false, this.checkvalidate(formValidator, obj));

        obj.put("list", new ArrayList<>());
        assertEquals(false, this.checkvalidate(formValidator, obj));

        Quiz quiz = new Quiz();
        quiz.setName("Quiz");
        quiz.setDescription("Description");
        quiz = quizRepository.save(quiz);
        obj.put("ref", (int) quiz.getId());
        assertEquals(true, this.checkvalidate(formValidator, obj));
    }
}
