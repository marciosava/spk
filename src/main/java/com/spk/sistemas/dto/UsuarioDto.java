package com.spk.sistemas.dto;

import java.util.List;

public class UsuarioDto {
    private Long id;
    private String nome;
    private String username;
    private String email;
    private boolean ativo;
    
    private List<String> roleNames;
    
    public List<String> getRoleNames() {
        return roleNames;
    }
    
    
    // Getters e Setters

    public static class RoleDto {
        private String nome;

        public RoleDto(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }
    
    
}
