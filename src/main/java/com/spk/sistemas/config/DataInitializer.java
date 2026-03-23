package com.spk.sistemas.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.spk.sistemas.model.Role;
import com.spk.sistemas.model.Usuario;
import com.spk.sistemas.repository.RoleRepository;
import com.spk.sistemas.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner initializer(UsuarioRepository usuarioRepository,
                                         RoleRepository roleRepository,
                                         PasswordEncoder passwordEncoder) {

        return args -> {
            Role roleAdmin = roleRepository.findByNome("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

            Role roleUser = roleRepository.findByNome("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

            if (!usuarioRepository.existsByUsername("admin")) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setUsername("admin");
                admin.setEmail("admin@email.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setAtivo(true);
                admin.setDataAtivacao(LocalDate.now());
                admin.setRoles(List.of(roleAdmin));

                usuarioRepository.save(admin);

                System.out.println("✅ Usuário ADMIN criado: login 'admin' / senha 'admin123'");
            }

            if (!usuarioRepository.existsByUsername("user")) {
                Usuario user = new Usuario();
                user.setNome("Usuário Padrão");
                user.setUsername("user");
                user.setEmail("user@email.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setAtivo(true);
                user.setDataAtivacao(LocalDate.now());
                user.setRoles(List.of(roleUser));

                usuarioRepository.save(user);

                System.out.println("✅ Usuário USER criado: login 'user' / senha 'user123'");
            }
        };
    }
}