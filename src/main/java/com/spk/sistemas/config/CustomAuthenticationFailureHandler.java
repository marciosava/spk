package com.spk.sistemas.config;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
    	
        String errorMessage = "Login ou senha inválidos.";

        if (exception instanceof DisabledException) {
            errorMessage = "Usuário desativado. Contate o administrador.";
        } else if (exception instanceof AccountExpiredException) {
            errorMessage = "Conta expirada. Solicite reativação ao administrador.";
        }

        request.getSession().setAttribute("mensagemErro", errorMessage);
        getRedirectStrategy().sendRedirect(request, response, "/login");
    }
}