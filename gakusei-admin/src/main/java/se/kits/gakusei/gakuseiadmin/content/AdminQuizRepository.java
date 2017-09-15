package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import se.kits.gakusei.content.model.Quiz;

import java.util.ArrayList;
import java.util.List;

public interface AdminQuizRepository extends CrudRepository<Quiz, Long> {

    List<Quiz> findByName(String name);
    List<Quiz> findAll();

}
