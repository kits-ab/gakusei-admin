package se.kits.gakusei.gakuseiadmin.Util;

import org.springframework.data.repository.CrudRepository;
import se.kits.gakusei.content.repository.QuizRepository;

import java.util.HashMap;
import java.util.List;

public class FormValidator {

    private static final String NAME = "name";
    private static final String DATA_TYPE = "data_type";
    public static final String DATA_TYPE_INT = "int";
    public static final String DATA_TYPE_STRING = "string";
    public static final String DATA_TYPE_LIST = "list";
    private static final String REFERENCE = "reference";

    private HashMap<String, Object>[] obj_info_ls;

    public FormValidator(HashMap<String, Object>[] obj_info_ls) {
        this.obj_info_ls = obj_info_ls;
    }

    public void validate(HashMap<String, Object> obj) throws FormException {
        HashMap<String, Object> errors = new HashMap<>();

        for (HashMap<String, Object> obj_info : this.obj_info_ls) {
            String error = this.validate_key(obj_info, obj);
            if (!error.isEmpty())
                errors.put((String) obj_info.get(this.NAME), error);
        }

        if (!errors.isEmpty())
            throw new FormException(errors);
    }

    private String validate_key(HashMap<String, Object> obj_info, HashMap<String, Object> obj) {
        String key = (String) obj_info.get(this.NAME);
        String data_type = (String) obj_info.get(this.DATA_TYPE);

        if (!obj.containsKey(key))
            return "Missing";

        if (data_type.equals(this.DATA_TYPE_INT)) {
            if (!(obj.get(key) instanceof Integer))
                return "Wrong type, should be a int";
        } else if (data_type.equals(this.DATA_TYPE_STRING)) {
            if (!(obj.get(key) instanceof String))
                return "Wrong type, should be a string";
        } else if (data_type.equals(this.DATA_TYPE_LIST)) {
            if (!(obj.get(key) instanceof List<?>))
                return "Wrong type, should be a list";
        } else
            return "Wrong type";

        if (obj_info.containsKey(this.REFERENCE)) {
            CrudRepository<?, Long> repository = (CrudRepository<?, Long>) obj_info.get(this.REFERENCE);
            if (!repository.exists(new Long((Integer) obj.get(key))))
                return "Does not exist";
        }

        return "";
    }

    public static HashMap<String, Object> createObjInfo(String name, String data_type,
                                                        CrudRepository<?, Long> repository) {
        HashMap<String, Object> obj_info = new HashMap<>();
        obj_info.put(NAME, name);
        obj_info.put(DATA_TYPE, data_type);
        if (repository != null)
            obj_info.put(REFERENCE, repository);
        return obj_info;
    }

    public static class FormException extends Exception {
        private HashMap<String, Object> err_map;

        public FormException() { super(); }
        public FormException(HashMap<String, Object> err_map) { this.err_map = err_map; }

        public HashMap<String, Object> getErrMap() { return this.err_map; }
    }
}
