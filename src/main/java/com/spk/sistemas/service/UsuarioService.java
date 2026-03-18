package com.spk.sistemas.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spk.sistemas.dto.UsuarioTableDTO;
import com.spk.sistemas.model.Usuario;
import com.spk.sistemas.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // =========================================================
    // CONSULTAS
    // =========================================================

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public boolean existePorUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    // =========================================================
    // SALVAR
    // =========================================================

    @Transactional
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            return salvarNovoUsuario(usuario);
        }

        return atualizarUsuarioExistente(usuario);
    }

    private Usuario salvarNovoUsuario(Usuario usuario) {
        if (usuario.getDataAtivacao() == null && usuario.isAtivo()) {
            usuario.setDataAtivacao(LocalDate.now());
        }

        if (!usuario.isAtivo() && usuario.getDataDesativacao() == null) {
            usuario.setDataDesativacao(LocalDate.now());
        }

        return usuarioRepository.save(usuario);
    }

    private Usuario atualizarUsuarioExistente(Usuario usuario) {
        Usuario existente = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        boolean statusAlterado = existente.isAtivo() != usuario.isAtivo();

        existente.setNome(usuario.getNome());
        existente.setUsername(usuario.getUsername());
        existente.setEmail(usuario.getEmail());
        existente.setCpf(usuario.getCpf());
        existente.setDataNascimento(usuario.getDataNascimento());
        existente.setDataExpiracao(usuario.getDataExpiracao());
        existente.setRoles(usuario.getRoles());
        existente.setAtivo(usuario.isAtivo());

        // Preserva senha atual se já existir
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            existente.setPassword(usuario.getPassword());
        }

        if (statusAlterado) {
            if (usuario.isAtivo()) {
                existente.setDataDesativacao(null);

                if (existente.getDataAtivacao() == null) {
                    existente.setDataAtivacao(LocalDate.now());
                }
            } else {
                existente.setDataDesativacao(LocalDate.now());
            }
        }

        return usuarioRepository.save(existente);
    }

    // =========================================================
    // EXCLUIR
    // =========================================================

    @Transactional
    public void excluirPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        usuarioRepository.delete(usuario);
    }

    // =========================================================
    // ATIVAR / DESATIVAR
    // =========================================================

    @Transactional
    public boolean alternarStatusAtivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        usuario.setAtivo(!usuario.isAtivo());

        if (usuario.isAtivo()) {
            usuario.setDataDesativacao(null);

            if (usuario.getDataAtivacao() == null) {
                usuario.setDataAtivacao(LocalDate.now());
            }
        } else {
            usuario.setDataDesativacao(LocalDate.now());
        }

        usuarioRepository.save(usuario);
        return usuario.isAtivo();
    }

    // =========================================================
    // DATATABLES
    // =========================================================

    public Map<String, Object> listarTodosParaDataTables(int draw,
                                                         int start,
                                                         int length,
                                                         String searchValue,
                                                         Integer orderColumn,
                                                         String orderDir) {

        List<UsuarioTableDTO> todos = usuarioRepository.findAll()
                .stream()
                .map(UsuarioTableDTO::new)
                .collect(Collectors.toList());

        int recordsTotal = todos.size();

        List<UsuarioTableDTO> filtrados = filtrarUsuarios(todos, searchValue);
        ordenarUsuarios(filtrados, orderColumn, orderDir);

        int recordsFiltered = filtrados.size();

        List<UsuarioTableDTO> paginados = paginarUsuarios(filtrados, start, length);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", recordsTotal);
        response.put("recordsFiltered", recordsFiltered);
        response.put("data", paginados);

        return response;
    }

    private List<UsuarioTableDTO> filtrarUsuarios(List<UsuarioTableDTO> usuarios, String searchValue) {
        if (searchValue == null || searchValue.isBlank()) {
            return new ArrayList<>(usuarios);
        }

        String filtro = searchValue.trim().toLowerCase();

        return usuarios.stream()
                .filter(u ->
                        contem(u.getId(), filtro)
                        || contem(u.getNome(), filtro)
                        || contem(u.getUsername(), filtro)
                        || contem(u.getEmail(), filtro)
                        || contem(u.isAtivo() ? "ativo" : "inativo", filtro)
                        || contem(u.getRoles(), filtro)
                )
                .collect(Collectors.toList());
    }

    private void ordenarUsuarios(List<UsuarioTableDTO> usuarios, Integer orderColumn, String orderDir) {
        Comparator<UsuarioTableDTO> comparator = obterComparator(orderColumn);

        if ("desc".equalsIgnoreCase(orderDir)) {
            comparator = comparator.reversed();
        }

        usuarios.sort(comparator);
    }

    private Comparator<UsuarioTableDTO> obterComparator(Integer orderColumn) {
        if (orderColumn == null) {
            return Comparator.comparing(
                    UsuarioTableDTO::getId,
                    Comparator.nullsLast(Long::compareTo)
            );
        }

        switch (orderColumn) {
            case 0:
                return Comparator.comparing(
                        UsuarioTableDTO::getId,
                        Comparator.nullsLast(Long::compareTo)
                );
            case 1:
                return Comparator.comparing(
                        UsuarioTableDTO::getNome,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                );
            case 2:
                return Comparator.comparing(
                        UsuarioTableDTO::getUsername,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                );
            case 3:
                return Comparator.comparing(
                        UsuarioTableDTO::getEmail,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                );
            case 4:
                return Comparator.comparing(UsuarioTableDTO::isAtivo);
            case 5:
                return Comparator.comparing(
                        UsuarioTableDTO::getRoles,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                );
            default:
                return Comparator.comparing(
                        UsuarioTableDTO::getId,
                        Comparator.nullsLast(Long::compareTo)
                );
        }
    }

    private List<UsuarioTableDTO> paginarUsuarios(List<UsuarioTableDTO> usuarios, int start, int length) {
        if (usuarios.isEmpty()) {
            return new ArrayList<>();
        }

        if (start < 0) {
            start = 0;
        }

        if (length <= 0) {
            length = usuarios.size();
        }

        int fromIndex = Math.min(start, usuarios.size());
        int toIndex = Math.min(start + length, usuarios.size());

        return new ArrayList<>(usuarios.subList(fromIndex, toIndex));
    }

    private boolean contem(Object valor, String filtro) {
        return valor != null && valor.toString().toLowerCase().contains(filtro);
    }
}