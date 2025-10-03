package com.spk.sistemas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spk.sistemas.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
}