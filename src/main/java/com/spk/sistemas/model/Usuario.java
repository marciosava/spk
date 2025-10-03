package com.spk.sistemas.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuario")
public class Usuario extends AbstractEntity<Long> implements UserDetails {

    private static final long serialVersionUID = 1L;

    @NotNull
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres.")
    private String nome;

	private LocalDate dataAtivacao;
	
	@Column(name = "data_desativacao")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataDesativacao;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataExpiracao;   
    
    @CPF(message = "CPF inválido.")
    private String cpf;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dataNascimento;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Informe um e-mail válido")
    private String email;

	@NotBlank(message = "A senha deve ser informada.")
    private String password;

    @NotEmpty(message = "O login deve ser informado.")
    @Size(min = 4, message = "O login deve ter no mínimo 4 caracteres.")
    @Column(nullable=true, unique = true) // Garante que o login seja único no banco
    private String username; // Alterado de 'login' para 'username', para alinhar com UserDetails

    private boolean ativo = true; // Valor defult

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_role",
               joinColumns = @JoinColumn(name = "usuario_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    // --- Implementação do UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLogin(String login) { // Mantido para compatibilidade
        this.username = login;
    }   
    
    // --- Getters e Setters convencionais ---
    public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUsername(String username) {
		this.username = username;
	}  
    
	@Override
	public String getUsername() {
		return this.username;
	}
	
    @Override
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Pode ser customizado
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Pode ser customizado
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Pode ser customizado
    }

    @Override
    public boolean isEnabled() {
        return this.ativo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

	public LocalDate getDataAtivacao() {
		return dataAtivacao;
	}

	public void setDataAtivacao(LocalDate dataAtivacao) {
		this.dataAtivacao = dataAtivacao;
	}

	public LocalDate getDataDesativacao() {
		return dataDesativacao;
	}

	public void setDataDesativacao(LocalDate dataDesativacao) {
		this.dataDesativacao = dataDesativacao;
	}

	public LocalDate getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(LocalDate dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}
    
    // Método auxiliar para obter os nomes das roles
    public List<String> getRoleNames() {
        if (roles == null) return Collections.emptyList();
        return roles.stream()
                  .map(Role::getAuthority) // ou getNome(), dependendo da sua classe Role
                  .map(name -> name.replace("ROLE_", ""))
                  .collect(Collectors.toList());
    }

}
