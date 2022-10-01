package com.example.Vox.Viridis.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import com.example.Vox.Viridis.service.JpaUserDetailsService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
        private final RsaKeyProperties rsaKeys;
        private final JpaUserDetailsService myUserDetailsService;

        public SecurityConfig(RsaKeyProperties rsaKeys, JpaUserDetailsService myUsersService) {
                this.rsaKeys = rsaKeys;
                this.myUserDetailsService = myUsersService;
        }

        @Bean
        public InMemoryUserDetailsManager user() {
                return new InMemoryUserDetailsManager(
                                User.withUsername("consumer").password("{noop}passwordC")
                                                .authorities("consumer").build(),
                                User.withUsername("admin").password("passwordA")
                                                .authorities("admin").build());
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeRequests(auth -> auth.antMatchers("/users/save").permitAll()
                                .anyRequest().authenticated()).csrf(csrf -> csrf.disable())
                                .httpBasic(Customizer.withDefaults())
                                .userDetailsService(myUserDetailsService)
                                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                                .sessionManagement((session) -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS));
                return http.build();
        }

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        JwtDecoder jwtDecoder() {
                return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
        }

        @Bean
        JwtEncoder jwtEncoder() {
                JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey())
                                .build();
                JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
                return new NimbusJwtEncoder(jwks);
        }
}

