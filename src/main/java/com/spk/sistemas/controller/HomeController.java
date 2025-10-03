package com.spk.sistemas.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirecionarParaDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping("/home")
    public String homePublica() {
        return "home"; // Ex: uma página pública ou institucional
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado"; // templates/acesso-negado.html
    }
    
    @GetMapping("/dashboard/admin")
    public String dashboardAdmin() {
        return "dashboard/admin"; // → templates/dashboard/admin.html
    }

    @GetMapping("/dashboard/usuario")
    public String dashboardUsuario() {
        return "dashboard/usuario"; // → templates/dashboard/usuario.html
    }
}
