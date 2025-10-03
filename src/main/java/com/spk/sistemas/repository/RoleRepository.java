package com.spk.sistemas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spk.sistemas.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNome(String nome);
}