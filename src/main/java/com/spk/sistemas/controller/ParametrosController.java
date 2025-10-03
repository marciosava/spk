package com.spk.sistemas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/parametros")
public class ParametrosController {

    @GetMapping
    public String abrirPaginaParametros() {
        return "parametros/parametros"; // <- layout com sidebar e introdução
    }
}

