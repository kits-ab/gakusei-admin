package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import se.kits.gakusei.gakuseiadmin.content.AdminUserRepository;
import se.kits.gakusei.user.model.Event;
import se.kits.gakusei.user.model.User;
import se.kits.gakusei.user.repository.EventRepository;
import se.kits.gakusei.user.repository.UserRepository;

import java.security.Principal;
import java.sql.Timestamp;

@RestController
public class AdminUserController {

    @Autowired
    AdminUserRepository adminUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EventRepository eventRepository;

    private final String NO_SEARCHSTRING_PROVIDED = "NO_SEARCHSTRING_PROVIDED";
    private final String NO_ROLE_PROVIDED = "NO_ROLE_PROVIDED";

    @RequestMapping(
            value = "/api/users/{searchString}/{role}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Iterable<User>> searchUserFilterByRole(@PathVariable(value = "searchString") String searchString,
                                                                 @PathVariable(value = "role") String role,
                                                                 Principal principal) {
        logEvent(adminUserRepository.findOne(principal.getName()), searchString + " : " + role, "SEARCH");

        if(searchString.equals(NO_SEARCHSTRING_PROVIDED)){
            searchString = "";
        }

        if(role.equals(NO_ROLE_PROVIDED)){
            role = "";
        }

        return new ResponseEntity<>(adminUserRepository.findByUsernameContainingIgnoreCaseAndRoleContainingIgnoreCase(searchString, role), HttpStatus.OK);
    }

    @RequestMapping(
            value = "/api/users/password",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<User> resetPassword(@RequestBody User user,
                                              Principal principal){
        User toUpdate = adminUserRepository.findOne(user.getUsername());
        if(toUpdate != null) {
            logEvent(adminUserRepository.findOne(principal.getName()), user.getUsername(), "PASSWORD");
            toUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
            return new ResponseEntity<>(adminUserRepository.save(toUpdate), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/api/users/role",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<User> changeRole(@RequestBody User user,
                                           Principal principal){
        User toUpdate = adminUserRepository.findOne(user.getUsername());
        if(toUpdate != null) {
            logEvent(adminUserRepository.findOne(principal.getName()), toUpdate.getUsername() + " : " + toUpdate.getRole() + " -> " + user.getRole(), "ROLE");
            toUpdate.setRole(user.getRole());
            return new ResponseEntity<>(adminUserRepository.save(toUpdate), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(
            value = "/api/users/{username}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<User> deleteUser(@PathVariable(value = "username") String username,
                                           Principal principal){
        if(adminUserRepository.exists(username)) {
            logEvent(adminUserRepository.findOne(principal.getName()), username, "DELETE");
            adminUserRepository.delete(username);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private void logEvent(User user, String action, String type){
        Event event = new Event();
        event.setTimestamp(new Timestamp(System.currentTimeMillis()));
        event.setGamemode("Administration");
        event.setNuggetId(null);
        event.setType(type);
        event.setUser(user);
        event.setData(action);

        eventRepository.save(event);
    }
}
