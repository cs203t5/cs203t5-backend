package com.example.Vox.Viridis.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
        @Bean
        public InMemoryUserDetailsManager user() {
                return new InMemoryUserDetailsManager(
                                User.withUsername("consumer").password("{noop}passwordC")
                                                .authorities("consumer").build(),
                                User.withUsername("admin").password("{noop}passwordA")
                                                .authorities("admin").build());
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeRequests(auth -> auth.antMatchers("/users/save").permitAll()
                                .anyRequest().authenticated()).csrf(csrf -> csrf.disable())
                                .httpBasic(Customizer.withDefaults())
                                .sessionManagement((session) -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS));
                return http.build();
        }

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }
}

