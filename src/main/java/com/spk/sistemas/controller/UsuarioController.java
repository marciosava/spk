package com.spk.sistemas.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spk.sistemas.model.Usuario;
import com.spk.sistemas.service.RoleService;
import com.spk.sistemas.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

import com.spk.sistemas.dto.AlterarSenhaDto;
import jakarta.validation.Valid;
import org.springframework.validation.FieldError;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    // ✅ Página HTML da lista de usuários
    @GetMapping
    public String listarUsuarios() {
        return "usuarios/lista-usuario";
    }

    // ✅ Endpoint para o DataTables via AJAX
    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> listarUsuariosDataTables(HttpServletRequest request) {
        return (Map<String, Object>) usuarioService.listarTodosParaDataTables();
    }

    // ✅ Página de formulário para novo usuário
    @GetMapping("/cadastrar")
    public String novoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("todosRoles", roleService.findAll());
        return "usuarios/form-usuario";
    }

    // ✅ Editar usuário existente
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        model.addAttribute("usuario", usuario);
        model.addAttribute("todosRoles", roleService.findAll());
        return "usuarios/form-usuario";
    }

    // ✅ Salvar (novo ou edição) — mensagens 100% SweetAlert
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("usuario") Usuario usuario,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        boolean novo = (usuario.getId() == null);

        // Se houver erros de validação, volta para o form e exibe SweetAlert de erro
        if (result.hasErrors()) {
            model.addAttribute("todosRoles", roleService.findAll());
            model.addAttribute("msgErro", "Corrija os erros do formulário antes de salvar.");
            return "usuarios/form-usuario";
        }

        try {
            if (novo) {
                // Define senha padrão para novos usuários
                usuario.setPassword(passwordEncoder.encode("123456"));
            } else {
                // Preserva a senha existente ao editar
                Optional<Usuario> existenteOpt = usuarioService.buscarPorId(usuario.getId());
                if (existenteOpt.isPresent()) {
                    Usuario existente = existenteOpt.get();
                    usuario.setPassword(existente.getPassword());
                } else {
                    model.addAttribute("todosRoles", roleService.findAll());
                    model.addAttribute("msgErro", "Usuário não encontrado para edição.");
                    return "usuarios/form-usuario";
                }
            }

            usuarioService.salvar(usuario);
            redirectAttributes.addFlashAttribute("msgSucesso", "Usuário salvo com sucesso!");
            return "redirect:/usuarios";

        } catch (Exception e) {
            // Em caso de falha inesperada, volta ao form com erro
            model.addAttribute("todosRoles", roleService.findAll());
            model.addAttribute("msgErro", "Não foi possível salvar o usuário. Tente novamente.");
            return "usuarios/form-usuario";
        }
    }

    // ✅ Compatibilidade para links antigos
    @GetMapping({"/altera-senha", "/minha-senha"})
    public String compatAlterarSenha() {
        return "redirect:/usuarios/alterar-senha";
    }

    @ModelAttribute("alterarSenhaDto")
    public AlterarSenhaDto alterarSenhaDtoModelAttr() {
        return new AlterarSenhaDto();
    }
    
    // ✅ Formulário de alteração de senha
    @GetMapping("/alterar-senha")
    public String exibirFormularioAlterarSenha() {
        return "usuarios/alterar-senha";
    }

    @PostMapping("/alterar-senha")
    public String alterarSenhaUsuarioLogado(@Valid @ModelAttribute("alterarSenhaDto") AlterarSenhaDto alterarSenhaDto,
                                            BindingResult errors,
                                            Principal principal,
                                            RedirectAttributes ra,
                                            Model model) {
        try {
            // 1) Validação de confirmação
            if (!errors.hasFieldErrors("confirmacaoSenha")
                    && alterarSenhaDto.getNovaSenha() != null
                    && !alterarSenhaDto.getNovaSenha().equals(alterarSenhaDto.getConfirmacaoSenha())) {
                errors.addError(new FieldError("alterarSenhaDto", "confirmacaoSenha",
                        "A confirmação deve ser igual à nova senha."));
            }

            // 2) Validação de tamanho mínimo (já tem @Size, mas reforça regra de negócio se quiser)
            if (!errors.hasFieldErrors("novaSenha")
                    && alterarSenhaDto.getNovaSenha() != null
                    && alterarSenhaDto.getNovaSenha().length() < 6) {
                errors.addError(new FieldError("alterarSenhaDto", "novaSenha",
                        "A nova senha deve ter ao menos 6 caracteres."));
            }

            // 3) Buscar usuário logado
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsername(principal.getName());
            if (usuarioOpt.isEmpty()) {
                errors.addError(new FieldError("alterarSenhaDto", "senhaAtual",
                        "Usuário não encontrado."));
            } else {
                // 4) Validar senha atual
                Usuario usuario = usuarioOpt.get();
                if (!passwordEncoder.matches(alterarSenhaDto.getSenhaAtual(), usuario.getPassword())) {
                    errors.addError(new FieldError("alterarSenhaDto", "senhaAtual",
                            "Senha atual incorreta."));
                }
            }

            // Se houver erros, volta para a página (Thymeleaf exibirá os th:errors)
            if (errors.hasErrors()) {
                model.addAttribute("msgErro", "Corrija os erros do formulário.");
                return "usuarios/alterar-senha";
            }

            // 5) Persistir nova senha
            Usuario usuario = usuarioOpt.get();
            usuario.setPassword(passwordEncoder.encode(alterarSenhaDto.getNovaSenha()));
            usuarioService.salvar(usuario);

            ra.addFlashAttribute("msgSucesso", "Senha alterada com sucesso!");
            return "redirect:/usuarios/alterar-senha";

        } catch (Exception e) {
            ra.addFlashAttribute("msgErro", "Não foi possível alterar a senha. Tente novamente.");
            return "redirect:/usuarios/alterar-senha";
        }
    }
    
    
    // ✅ Ativar / Desativar usuário — mensagens 100% SweetAlert
    @GetMapping("/ativar-desativar/{id}")
    public String ativarDesativarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean novoStatus = usuarioService.alternarStatusAtivo(id);
            String mensagem = novoStatus ? "Usuário ativado com sucesso!" : "Usuário desativado com sucesso!";
            redirectAttributes.addFlashAttribute("msgSucesso", mensagem);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msgErro", "Erro ao alterar status: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
