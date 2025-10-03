package com.spk.sistemas.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.spk.sistemas.model.Role;
import com.spk.sistemas.model.Usuario;

public class UsuarioTableDTO {
    private Long id;
    private String nome;
    private String username;
    private String email;
    private String roles; // String formatada para exibição
    private boolean ativo;
    
    // Construtor que recebe a entidade Usuario
    public UsuarioTableDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        this.roles = formatarRoles(usuario.getRoles());
        this.ativo = usuario.isAtivo();
    }
    
    private String formatarRoles(List<Role> list) {
        if (list == null || list.isEmpty()) {
            return "Nenhum perfil";
        }
        return list.stream()
                   .map(Role::getNome)
                   .collect(Collectors.joining(", "));
    }

    //-- getters e setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
    
    //----- Métodos auxiliares -----//
    @Override
    public String toString() {
        return "UsuarioTableDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles='" + roles + '\'' +
                ", ativo=" + ativo +
                '}';
    }	
	
}