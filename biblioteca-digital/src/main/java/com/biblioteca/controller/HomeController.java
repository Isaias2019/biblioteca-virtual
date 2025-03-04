package com.biblioteca.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"", "/", "/index", "/home"})
    public String home() {
        return "redirect:/livros";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
} 