package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import se.kits.gakusei.content.model.Fact;
import se.kits.gakusei.content.model.Nugget;
import java.util.List;

public interface AdminFactRepository extends CrudRepository<Fact, Long> {

    @Query("select distinct f.type from Fact f")
    List<String> getAllFactTypes();

    @Query("select f.nugget from Fact f where f.data = :d")
    List<Nugget> findNuggetsByFactData(@Param("d") String data);
}
