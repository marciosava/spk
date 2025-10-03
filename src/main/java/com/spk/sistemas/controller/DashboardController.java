package com.spk.sistemas.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String redirecionarDashboard(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return "redirect:/acesso-negado";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean isUser = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

        if (isAdmin) {
            return "redirect:/dashboard/admin";
        } else if (isUser) {
            return "redirect:/dashboard/usuario";
        }

        return "redirect:/acesso-negado";
    }
}
