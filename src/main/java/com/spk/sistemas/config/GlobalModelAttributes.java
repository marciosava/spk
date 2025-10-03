package com.spk.sistemas.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("uri")
    public String addUriToModel(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
