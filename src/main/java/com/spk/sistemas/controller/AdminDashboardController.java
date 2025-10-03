package com.spk.sistemas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    @GetMapping("/admin")
    public String dashboardUsuario() {
        return "dashboard/admin";  // caminho da view dentro de templates/
    }
}
