package com.spk.sistemas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsuarioDashboardController {

    @GetMapping("/usuario")
    public String dashboardUsuario() {
        return "dashboard/usuario";  // caminho da view dentro de templates/
    }
}
