package com.spk.sistemas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/menu")
    public String menu() {
        return "menu"; // resolve templates/menu.html
    }    
    
}
