package com.spk.sistemas.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@ConditionalOnMissingBean(CustomErrorController.class)
public class ErroController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        return "erro/pagina-de-erro";
    }
}
