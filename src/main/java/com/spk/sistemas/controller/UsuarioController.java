package com.spk.sistemas.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spk.sistemas.dto.AlterarSenhaDto;
import com.spk.sistemas.model.Role;
import com.spk.sistemas.model.Usuario;
import com.spk.sistemas.service.RoleService;
import com.spk.sistemas.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UsuarioController(UsuarioService usuarioService,
                             PasswordEncoder passwordEncoder,
                             RoleService roleService) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    
    
    //===================== testes =============================
    @GetMapping("/teste")
    @ResponseBody
    public List<Map<String, Object>> testeUsuarios() {
        return usuarioService.listarTodos()
                .stream()
                .map(usuario -> {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", usuario.getId());
                    item.put("nome", usuario.getNome());
                    item.put("username", usuario.getUsername());
                    item.put("email", usuario.getEmail());
                    item.put("ativo", usuario.isAtivo());
                    item.put("roles", usuario.getRoles()
                            .stream()
                            .map(role -> role.getNome())
                            .collect(java.util.stream.Collectors.joining(", ")));
                    return item;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    // =========================================================
    // LISTAGEM
    // =========================================================

    @GetMapping
    public String listarUsuarios() {
        return "usuarios/lista-usuario";
    }

    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> listarUsuariosDataTables(
            @RequestParam(name = "draw", defaultValue = "1") int draw,
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "length", defaultValue = "10") int length,
            @RequestParam(name = "search[value]", required = false) String searchValue,
            @RequestParam(name = "order[0][column]", required = false) Integer orderColumn,
            @RequestParam(name = "order[0][dir]", required = false) String orderDir,
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "ativo", required = false) String ativo) {

        try {
            return usuarioService.listarTodosParaDataTables(
                    draw,
                    start,
                    length,
                    normalizarTexto(nome),
                    converterAtivo(ativo),
                    normalizarTexto(searchValue),
                    orderColumn,
                    normalizarDirecao(orderDir)
            );
        } catch (Exception e) {
            return Map.of(
                    "draw", draw,
                    "recordsTotal", 0,
                    "recordsFiltered", 0,
                    "data", Collections.emptyList(),
                    "error", "Erro ao carregar usuários."
            );
        }
    }

    // =========================================================
    // NOVO / EDITAR
    // =========================================================

    @GetMapping({"/novo", "/cadastrar"})
    public String novoUsuario(Model model) {
        Usuario usuario = new Usuario();
        usuario.setAtivo(true);

        model.addAttribute("usuario", usuario);
        carregarDadosFormulario(model, Collections.emptyList());
        return "usuarios/form-usuario";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);

        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("msgErro", "Usuário não encontrado.");
            return "redirect:/usuarios";
        }

        Usuario usuario = usuarioOpt.get();
        model.addAttribute("usuario", usuario);
        carregarDadosFormulario(model, extrairRoleIds(usuario));

        return "usuarios/form-usuario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("usuario") Usuario usuario,
                         @RequestParam(name = "roleIds", required = false) List<Long> roleIds,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        boolean novo = (usuario.getId() == null);

        List<Role> rolesSelecionadas = resolverRoles(roleIds);
        usuario.setRoles(rolesSelecionadas);

        validarFormulario(usuario, roleIds, result);

        if (result.hasErrors()) {
            carregarDadosFormulario(model, roleIds);
            model.addAttribute("msgErro", "Corrija os erros do formulário antes de salvar.");
            return "usuarios/form-usuario";
        }

        try {
            if (novo) {
                usuario.setPassword(passwordEncoder.encode("123456"));
            } else {
                Optional<Usuario> existenteOpt = usuarioService.buscarPorId(usuario.getId());

                if (existenteOpt.isEmpty()) {
                    carregarDadosFormulario(model, roleIds);
                    model.addAttribute("msgErro", "Usuário não encontrado para edição.");
                    return "usuarios/form-usuario";
                }

                usuario.setPassword(existenteOpt.get().getPassword());

                if (usuario.getDataAtivacao() == null) {
                    usuario.setDataAtivacao(existenteOpt.get().getDataAtivacao());
                }
            }

            usuarioService.salvar(usuario);
            redirectAttributes.addFlashAttribute("msgSucesso", "Usuário salvo com sucesso!");
            return "redirect:/usuarios";

        } catch (Exception e) {
            carregarDadosFormulario(model, roleIds);
            model.addAttribute("msgErro", "Não foi possível salvar o usuário. Tente novamente.");
            return "usuarios/form-usuario";
        }
    }

    // =========================================================
    // EXCLUIR
    // =========================================================

    @GetMapping("/excluir/{id}")
    public String excluirUsuario(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            usuarioService.excluirPorId(id);
            redirectAttributes.addFlashAttribute("msgSucesso", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msgErro", "Não foi possível excluir o usuário.");
        }
        return "redirect:/usuarios";
    }

    // =========================================================
    // ATIVAR / DESATIVAR
    // =========================================================

    @GetMapping("/ativar-desativar/{id}")
    public String ativarDesativarUsuario(@PathVariable Long id,
                                         RedirectAttributes redirectAttributes) {
        try {
            boolean novoStatus = usuarioService.alternarStatusAtivo(id);

            redirectAttributes.addFlashAttribute(
                    "msgSucesso",
                    novoStatus ? "Usuário ativado com sucesso!" : "Usuário desativado com sucesso!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msgErro", "Erro ao alterar status do usuário.");
        }

        return "redirect:/usuarios";
    }

    // =========================================================
    // ALTERAR SENHA
    // =========================================================

    @GetMapping({"/altera-senha", "/minha-senha"})
    public String compatAlterarSenha() {
        return "redirect:/usuarios/alterar-senha";
    }

    @ModelAttribute("alterarSenhaDto")
    public AlterarSenhaDto alterarSenhaDtoModelAttr() {
        return new AlterarSenhaDto();
    }

    @GetMapping("/alterar-senha")
    public String exibirFormularioAlterarSenha() {
        return "usuarios/alterar-senha";
    }

    @PostMapping("/alterar-senha")
    public String alterarSenhaUsuarioLogado(
            @Valid @ModelAttribute("alterarSenhaDto") AlterarSenhaDto alterarSenhaDto,
            BindingResult errors,
            Principal principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsername(principal.getName());

        if (usuarioOpt.isEmpty()) {
            errors.addError(new FieldError(
                    "alterarSenhaDto",
                    "senhaAtual",
                    "Usuário não encontrado."
            ));
        } else {
            Usuario usuario = usuarioOpt.get();

            if (!passwordEncoder.matches(alterarSenhaDto.getSenhaAtual(), usuario.getPassword())) {
                errors.addError(new FieldError(
                        "alterarSenhaDto",
                        "senhaAtual",
                        "Senha atual incorreta."
                ));
            }

            if (alterarSenhaDto.getNovaSenha() != null
                    && alterarSenhaDto.getConfirmacaoSenha() != null
                    && !alterarSenhaDto.getNovaSenha().equals(alterarSenhaDto.getConfirmacaoSenha())) {
                errors.addError(new FieldError(
                        "alterarSenhaDto",
                        "confirmacaoSenha",
                        "A confirmação deve ser igual à nova senha."
                ));
            }
        }

        if (errors.hasErrors()) {
            model.addAttribute("msgErro", "Corrija os erros do formulário.");
            return "usuarios/alterar-senha";
        }

        try {
            Usuario usuario = usuarioOpt.get();
            usuario.setPassword(passwordEncoder.encode(alterarSenhaDto.getNovaSenha()));
            usuarioService.salvar(usuario);

            redirectAttributes.addFlashAttribute("msgSucesso", "Senha alterada com sucesso!");
            return "redirect:/usuarios/alterar-senha";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msgErro", "Não foi possível alterar a senha.");
            return "redirect:/usuarios/alterar-senha";
        }
    }

    // =========================================================
    // APOIO
    // =========================================================

    private void validarFormulario(Usuario usuario,
                                   List<Long> roleIds,
                                   BindingResult result) {

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            result.addError(new FieldError("usuario", "nome", "O nome é obrigatório."));
        }

        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            result.addError(new FieldError("usuario", "username", "O login é obrigatório."));
        }

        if (usuario.getUsername() != null && usuario.getUsername().trim().length() < 4) {
            result.addError(new FieldError("usuario", "username", "O login deve ter no mínimo 4 caracteres."));
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            result.addError(new FieldError("usuario", "email", "O e-mail é obrigatório."));
        }

        if (roleIds == null || roleIds.isEmpty()) {
            result.addError(new FieldError("usuario", "roles", "Selecione ao menos um perfil."));
        }

        if (usuarioService.usernameJaEmUso(usuario.getUsername(), usuario.getId())) {
            result.addError(new FieldError("usuario", "username", "Já existe um usuário com este login."));
        }

        if (usuarioService.emailJaEmUso(usuario.getEmail(), usuario.getId())) {
            result.addError(new FieldError("usuario", "email", "Já existe um usuário com este e-mail."));
        }
    }

    private List<Role> resolverRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        return roleService.findAll()
                .stream()
                .filter(role -> role.getId() != null && roleIds.contains(role.getId()))
                .collect(Collectors.toList());
    }

    private List<Long> extrairRoleIds(Usuario usuario) {
        if (usuario == null || usuario.getRoles() == null) {
            return Collections.emptyList();
        }

        return usuario.getRoles()
                .stream()
                .map(Role::getId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private void carregarDadosFormulario(Model model, List<Long> roleIdsSelecionados) {
        model.addAttribute("todosRoles", roleService.findAll());
        model.addAttribute("roleIdsSelecionados",
                roleIdsSelecionados != null ? roleIdsSelecionados : Collections.emptyList());
    }

    private String normalizarTexto(String valor) {
        if (valor == null) {
            return null;
        }
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }

    private Boolean converterAtivo(String ativo) {
        if (ativo == null || ativo.trim().isEmpty()) {
            return null;
        }
        return Boolean.valueOf(ativo);
    }

    private String normalizarDirecao(String orderDir) {
        if (orderDir == null || orderDir.isBlank()) {
            return "asc";
        }
        return "desc".equalsIgnoreCase(orderDir) ? "desc" : "asc";
    }
}