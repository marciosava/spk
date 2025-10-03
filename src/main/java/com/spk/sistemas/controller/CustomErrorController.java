package com.spk.sistemas.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute("jakarta.servlet.error.status_code");
        Object errorMessage = request.getAttribute("jakarta.servlet.error.message");
        Object exception = request.getAttribute("jakarta.servlet.error.exception");

        model.addAttribute("status", statusCode != null ? statusCode : "Erro");
        model.addAttribute("error", errorMessage != null ? errorMessage : "Erro inesperado.");
        model.addAttribute("message", exception != null ? exception : "");

        return "error/erro";
    }
}

