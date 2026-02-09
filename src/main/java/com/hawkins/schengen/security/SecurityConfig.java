package com.hawkins.schengen.security;

import com.hawkins.schengen.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http, PersistentTokenRepository tokenRepo) throws Exception {
                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers("/register", "/login", "/forgot-password", "/reset-password",
                                                "/css/**", "/js/**")
                                .permitAll()
                                .requestMatchers("/h2/**").permitAll()
                                .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated());

                http.csrf(csrf -> csrf
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .ignoringRequestMatchers("/h2/**"));

                http.headers(h -> h
                                .frameOptions(f -> f.sameOrigin())
                                .contentSecurityPolicy(csp -> csp.policyDirectives(
                                                "default-src 'self'; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline'"))
                                .referrerPolicy(r -> r.policy(
                                                org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                                .permissionsPolicyHeader(permissions -> permissions
                                                .policy("geolocation=(), microphone=(), camera=()")));

                http.formLogin(login -> login
                                .loginPage("/login").permitAll()
                                .defaultSuccessUrl("/", true));

                http.rememberMe(rm -> rm
                                .tokenRepository(tokenRepo)
                                .tokenValiditySeconds(60 * 60 * 24 * 14)
                                .rememberMeParameter("remember-me"));

                http.logout(logout -> logout
                                .logoutSuccessUrl("/login?logout")
                                .permitAll());

                return http.build();
        }

    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
                return username -> users.findByUsername(username)
                                .map(u -> User.withUsername(u.getUsername())
                                                .password(u.getPasswordHash())
                                                .disabled(!u.isEnabled())
                                                .roles(u.getRole())
                                                .build())
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

    @Bean
    PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

    @Bean
    PersistentTokenRepository persistentTokenRepository(JdbcTemplate jdbcTemplate) {
                JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
                repo.setJdbcTemplate(jdbcTemplate);
                return repo;
        }
}
