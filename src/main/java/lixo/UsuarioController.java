package lixo;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

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

    //--- este metodo serve de modelo para testar varias situacoes, liberado no SecurityConfig.java 
    //    por isso este mapeamento permite digitar direto no browser e executa-lo.
    //
//    @GetMapping("/teste")
//    @ResponseBody
//    public Map<String, Object> testeUsuarios(
//            @RequestParam(defaultValue = "1") int draw,
//            @RequestParam(defaultValue = "0") int start,
//            @RequestParam(defaultValue = "10") int length,
//            @RequestParam(required = false) String search,
//            @RequestParam(required = false) Integer orderColumn,
//            @RequestParam(required = false) String orderDir) {
//
//        return usuarioService.listarTodosParaDataTables(
//                draw, start, length, search, orderColumn, orderDir
//        );
//    }
    
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
            @RequestParam(name = "order[0][dir]", required = false) String orderDir) {

        return usuarioService.listarTodosParaDataTables(
                draw,
                start,
                length,
                searchValue,
                orderColumn,
                orderDir
        );
    }

    // =========================================================
    // NOVO / EDITAR
    // =========================================================

    @GetMapping({"/novo", "/cadastrar"})
    public String novoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        carregarDadosFormulario(model);
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

        model.addAttribute("usuario", usuarioOpt.get());
        carregarDadosFormulario(model);
        return "usuarios/form-usuario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("usuario") Usuario usuario,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        boolean novo = (usuario.getId() == null);

        if (result.hasErrors()) {
            carregarDadosFormulario(model);
            model.addAttribute("msgErro", "Corrija os erros do formulário antes de salvar.");
            return "usuarios/form-usuario";
        }

        try {
            if (novo) {
                // Senha padrão inicial para novo usuário
                usuario.setPassword(passwordEncoder.encode("123456"));
            } else {
                // Preserva senha atual na edição
                Optional<Usuario> existenteOpt = usuarioService.buscarPorId(usuario.getId());

                if (existenteOpt.isEmpty()) {
                    carregarDadosFormulario(model);
                    model.addAttribute("msgErro", "Usuário não encontrado para edição.");
                    return "usuarios/form-usuario";
                }

                Usuario existente = existenteOpt.get();
                usuario.setPassword(existente.getPassword());
            }

            usuarioService.salvar(usuario);
            redirectAttributes.addFlashAttribute("msgSucesso", "Usuário salvo com sucesso!");
            return "redirect:/usuarios";

        } catch (Exception e) {
            carregarDadosFormulario(model);
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
            String mensagem = novoStatus
                    ? "Usuário ativado com sucesso!"
                    : "Usuário desativado com sucesso!";

            redirectAttributes.addFlashAttribute("msgSucesso", mensagem);
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

        try {
            if (!errors.hasFieldErrors("confirmacaoSenha")
                    && alterarSenhaDto.getNovaSenha() != null
                    && !alterarSenhaDto.getNovaSenha().equals(alterarSenhaDto.getConfirmacaoSenha())) {
                errors.addError(new FieldError(
                        "alterarSenhaDto",
                        "confirmacaoSenha",
                        "A confirmação deve ser igual à nova senha."
                ));
            }

            if (!errors.hasFieldErrors("novaSenha")
                    && alterarSenhaDto.getNovaSenha() != null
                    && alterarSenhaDto.getNovaSenha().length() < 6) {
                errors.addError(new FieldError(
                        "alterarSenhaDto",
                        "novaSenha",
                        "A nova senha deve ter ao menos 6 caracteres."
                ));
            }

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
            }

            if (errors.hasErrors()) {
                model.addAttribute("msgErro", "Corrija os erros do formulário.");
                return "usuarios/alterar-senha";
            }

            Usuario usuario = usuarioOpt.get();
            usuario.setPassword(passwordEncoder.encode(alterarSenhaDto.getNovaSenha()));
            usuarioService.salvar(usuario);

            redirectAttributes.addFlashAttribute("msgSucesso", "Senha alterada com sucesso!");
            return "redirect:/usuarios/alterar-senha";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msgErro", "Não foi possível alterar a senha. Tente novamente.");
            return "redirect:/usuarios/alterar-senha";
        }
    }

    // =========================================================
    // APOIO
    // =========================================================

    private void carregarDadosFormulario(Model model) {
        model.addAttribute("todosRoles", roleService.findAll());
    }
}