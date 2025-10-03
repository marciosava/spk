package com.spk.sistemas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AlterarSenhaDto {
	@NotBlank(message = "Informe a senha atual.")
	private String senhaAtual;

	@NotBlank(message = "Informe a nova senha.")
	@Size(min = 8, message = "A nova senha deve ter ao menos 8 caracteres.")
	private String novaSenha;

	@NotBlank(message = "Confirme a nova senha.")
	private String confirmacaoSenha;

    /// getters e setters	
	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}

	public void setConfirmacaoSenha(String confirmacaoSenha) {
		this.confirmacaoSenha = confirmacaoSenha;
	}

}

