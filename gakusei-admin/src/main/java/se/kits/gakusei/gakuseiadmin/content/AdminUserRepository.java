package se.kits.gakusei.gakuseiadmin.content;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import se.kits.gakusei.user.model.User;

public interface AdminUserRepository extends CrudRepository<User, String> {

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Query("SELECT u FROM User u WHERE u.username LIKE CONCAT('%', :name, '%')")
    Iterable<User> findByNameContains(@Param("name") String name);

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Query("SELECT u FROM User u WHERE u.username LIKE CONCAT('%', :name, '%') AND u.role LIKE CONCAT('%', :role, '%')")
    Iterable<User> findByNameContainsFilterByRole(@Param("name") String name, @Param("role") String role);
}
