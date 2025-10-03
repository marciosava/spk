package com.spk.sistemas.service;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.spk.sistemas.model.Usuario;

public class UsuarioDetails implements UserDetails {

    private final Usuario usuario;

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles();
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    /**
     * Retorna false se a data de expiração for anterior a hoje.
     */
    @Override
    public boolean isAccountNonExpired() {
        // Se não houver data de expiração, considera não expirada
        if (usuario.getDataExpiracao() == null) return true;

        return !usuario.getDataExpiracao().isBefore(LocalDate.now());
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Pode implementar lógica futura se desejar
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Pode ser ajustado no futuro (ex: troca periódica de senha)
    }

    /**
     * Retorna false se o usuário estiver inativo.
     */
    @Override
    public boolean isEnabled() {
        return usuario.isAtivo();
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
