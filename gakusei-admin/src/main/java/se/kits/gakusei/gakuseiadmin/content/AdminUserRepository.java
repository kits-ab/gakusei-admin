package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import se.kits.gakusei.user.model.User;

public interface AdminUserRepository extends CrudRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.username LIKE CONCAT('%', :name, '%')")
    Iterable<User> findByNameContains(@Param("name") String name);
}
