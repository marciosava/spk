package com.spk.sistemas.model;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementação de UserDetails para adaptação da entidade Usuario
 * ao modelo exigido pelo Spring Security.
 */
public class UsuarioDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Usuario usuario;

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Retorna as roles (perfis) do usuário como authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles(); // Deve implementar GrantedAuthority
    }

    /**
     * Retorna a senha (criptografada) do usuário.
     */
    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    /**
     * Retorna o login/username do usuário.
     */
    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    /**
     * Indica se a conta está expirada.
     * Se a data de expiração for anterior a hoje, retorna false.
     */
    @Override
    public boolean isAccountNonExpired() {
        LocalDate dataExpiracao = usuario.getDataExpiracao();
        return dataExpiracao == null || !dataExpiracao.isBefore(LocalDate.now());
    }

    /**
     * Indica se a conta está bloqueada.
     * Retorna true por padrão. Personalize se precisar.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se as credenciais estão expiradas.
     * Retorna true por padrão. Personalize se desejar.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está habilitado (ativo).
     */
    @Override
    public boolean isEnabled() {
        return usuario.isAtivo();
    }

    /**
     * Retorna o objeto Usuario original, se necessário.
     */
    public Usuario getUsuario() {
        return usuario;
    }
}
