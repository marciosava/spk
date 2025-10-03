package com.spk.sistemas.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        Object erro = request.getSession().getAttribute("mensagemErro");

        if (erro != null) {
            model.addAttribute("mensagemErro", erro.toString());
            request.getSession().removeAttribute("mensagemErro"); // limpa após leitura
        }

        return "login";
    }
}

