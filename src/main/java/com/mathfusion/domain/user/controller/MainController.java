package com.mathfusion.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/main")
    public String main() {
        return "Main"; // Main.html로 이동
    }
}
