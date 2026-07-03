package com.ecommerce.admin.config;

import com.ecommerce.library.repository.AdminRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AdminConfiguration {

    @Bean
    public UserDetailsService userDetailsService(AdminRepository adminRepository) {
        return new AdminServiceConfig(adminRepository);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            UserDetailsService userDetailsService,
            BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // A mágica acontece aqui agora! Declaramos a cadeia de filtros como um Bean.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuração de Autorização de Rotas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/*").permitAll()
                        // DICA: use /** para proteger todas as sub-rotas de admin, ex: /admin/produtos/novo
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                // 2. Configuração da Página de Login
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/do-login")
                        .defaultSuccessUrl("/admin/index", true)
                        .permitAll()
                )
                // 3. Configuração de Logout
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/login?logout") // Corrigido o typo aqui!
                        .permitAll()
                );

        return http.build();
    }
}