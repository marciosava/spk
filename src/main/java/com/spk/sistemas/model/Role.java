package com.spk.sistemas.model;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="role")
public class Role extends AbstractEntity<Long> implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    private String nome;  // Antes: "papel" (agora alinhado ao termo "role")

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)  // Antes: "papeis"
    private List<Usuario> usuarios;

    // Construtores
    public Role() {}

    public Role(String nome) {
        this.nome = nome;
    }

    // Implementação do GrantedAuthority
    @Override
    public String getAuthority() {
        return this.nome;  // Exemplo: "ROLE_ADMIN", "ROLE_USER"
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
    
    
    
}