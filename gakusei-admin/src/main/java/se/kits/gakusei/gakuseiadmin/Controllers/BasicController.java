package se.kits.gakusei.gakuseiadmin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.kits.gakusei.gakuseiadmin.content.AdminFactRepository;


@RestController
public class BasicController {
    @Autowired
    private AdminFactRepository factRepository;
    @RequestMapping("/hello")
    public String hello() {
    for (String s : factRepository.getAllFactTypes()) {
        System.out.println(s);
    }
    return "hello worlds";
    }
}
