package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.repository.CrudRepository;
import se.kits.gakusei.user.model.User;

public interface AdminUserRepository extends CrudRepository<User, String> {

    Iterable<User> findByUsernameContainingIgnoreCaseAndRoleContainingIgnoreCase(String name, String role);

}
