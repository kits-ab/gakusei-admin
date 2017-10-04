package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.gakuseiadmin.content.AdminUserRepository;
import se.kits.gakusei.user.model.User;
import se.kits.gakusei.user.repository.UserRepository;

@RestController
public class AdminUserController {

    @Autowired
    AdminUserRepository adminUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final String NO_SEARCHSTRING_PROVIDED = "NO_SEARCHSTRING_PROVIDED";
    private final String NO_ROLE_PROVIDED = "NO_ROLE_PROVIDED";

    @RequestMapping(
            value = "/api/users/{searchString}/{role}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Iterable<User>> searchUserFilterByRole(@PathVariable(value = "searchString") String searchString,
                                                                 @PathVariable(value = "role") String role) {
        if(searchString.equals(NO_SEARCHSTRING_PROVIDED)){
            searchString = "";
        }

        if(role.equals(NO_ROLE_PROVIDED)){
            role = "";
        }

        return new ResponseEntity<>(adminUserRepository.findByNameContainsFilterByRole(searchString, role), HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/users",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<User> resetPassword(@RequestBody User user){
        if(adminUserRepository.exists(user.getUsername())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/api/users/{username}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<User> deleteUser(@PathVariable(value = "username") String username){
        if(adminUserRepository.exists(username)) {
            adminUserRepository.delete(username);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
