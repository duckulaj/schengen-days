package com.example.schengen.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SchengenController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
