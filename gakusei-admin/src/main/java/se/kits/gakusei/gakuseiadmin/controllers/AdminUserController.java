package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.kits.gakusei.gakuseiadmin.content.AdminUserRepository;
import se.kits.gakusei.user.model.User;

@RestController
public class AdminUserController {

    @Autowired
    AdminUserRepository adminUserRepository;

    @RequestMapping(
            value="/api/users/{searchString}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Iterable<User>> searchUser(@PathVariable(value = "searchString") String searchString){
        return new ResponseEntity<>(adminUserRepository.findByNameContains(searchString), HttpStatus.OK);
    }
}
