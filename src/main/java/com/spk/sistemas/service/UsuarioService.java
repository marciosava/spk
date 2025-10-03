package com.spk.sistemas.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spk.sistemas.dto.UsuarioTableDTO;
import com.spk.sistemas.model.Usuario;
import com.spk.sistemas.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Lista todos os usuários
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Busca por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Busca por username (nome de login)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

   
    @Transactional
    public void salvar(Usuario usuario) {
        if (usuario.getId() != null) {
            Usuario existente = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            boolean statusAlterado = existente.isAtivo() != usuario.isAtivo();

            existente.setNome(usuario.getNome());
            existente.setEmail(usuario.getEmail());
            existente.setCpf(usuario.getCpf());
            existente.setDataNascimento(usuario.getDataNascimento());
            existente.setAtivo(usuario.isAtivo());
            existente.setRoles(usuario.getRoles());
            existente.setDataExpiracao(usuario.getDataExpiracao());

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

            usuarioRepository.save(existente);
        } else {
            // Novo usuário
            usuario.setDataAtivacao(LocalDate.now());
            usuarioRepository.save(usuario);
        }
    }

    // Verifica se username já existe
    public boolean existePorUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    // Verifica se email já existe
    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    // Exclui usuário
    public void excluirPorId(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    @Transactional 
    public boolean alternarStatusAtivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        usuario.setAtivo(!usuario.isAtivo());
        
        if (usuario.isAtivo()) {
            usuario.setDataDesativacao(null);
        } else {
            usuario.setDataDesativacao(LocalDate.now());
        }
        
        usuarioRepository.save(usuario);
        return usuario.isAtivo();
    }
    
    public List<UsuarioTableDTO> listarTodosParaDataTables() {
        List<Usuario> usuarios = usuarioRepository.findAll(); // Ou seu método existente
        return usuarios.stream()
                      .map(UsuarioTableDTO::new)
                      .collect(Collectors.toList());
    }
 
    
}
