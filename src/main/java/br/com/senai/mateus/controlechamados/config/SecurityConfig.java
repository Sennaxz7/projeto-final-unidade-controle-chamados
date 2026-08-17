package br.com.senai.mateus.controlechamados.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsService usuarios() {
        UserDetails admin = User.builder()
                .username(System.getenv("SECURITY_USERNAME"))
                .password(passwordEncoder().encode(System.getenv("SECURITY_PASSWORD")))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain filtroSeguranca(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/chamados/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tecnicos/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/chamados/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/chamados/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/chamados/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/chamados/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/categorias/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/categorias/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/categorias/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/tecnicos/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/tecnicos/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/tecnicos/**").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
