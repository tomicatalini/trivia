package com.taller.trivia.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class Prueba {

    @GetMapping("/hello")
    public String sayHello(@RequestParam(name = "name", defaultValue = "", required = false) String name){
        if (name.isEmpty()) {
            name = "Anonimo";
        }

        return "Hola " + name + "!!! Que tengas una buena mañana!";
    }
}
