package com.example.Vox.Viridis.security;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.example.Vox.Viridis.service.JpaUserDetailsService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        private final RsaKeyProperties rsaKeys;
        private final JpaUserDetailsService myUserDetailsService;

        public SecurityConfig(RsaKeyProperties rsaKeys, JpaUserDetailsService myUsersService) {
                this.rsaKeys = rsaKeys;
                this.myUserDetailsService = myUsersService;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.cors(Customizer.withDefaults()).authorizeRequests(auth -> auth
                                .antMatchers("/users/save", "/swagger-ui/**", "/swagger-ui.html")
                                .permitAll()
                                // Campaign API
                                .antMatchers(HttpMethod.POST, "/campaign")
                                .hasAnyAuthority("SCOPE_BUSINESS")
                                .antMatchers(HttpMethod.PUT, "/campaign/*")
                                .hasAnyAuthority("SCOPE_BUSINESS")
                                .antMatchers(HttpMethod.DELETE, "/campaign/*")
                                .hasAnyAuthority("SCOPE_BUSINESS")
                                .antMatchers(HttpMethod.GET, "/campaign/*").permitAll()
                                .antMatchers(HttpMethod.GET, "/campaign").permitAll()
                                .antMatchers(HttpMethod.GET, "/campaign/myCampaign").hasAnyAuthority("SCOPE_BUSINESS")

                                // reward API
                                .antMatchers(HttpMethod.GET, "/campaign/*/reward").permitAll()
                                .antMatchers(HttpMethod.POST, "/campaign/*/reward")
                                .hasAnyAuthority("SCOPE_BUSINESS")
                                .antMatchers(HttpMethod.PUT, "/campaign/*/reward/*")
                                .hasAnyAuthority("SCOPE_BUSINESS")
                                .antMatchers(HttpMethod.DELETE, "/campaign/*/reward/*")
                                .hasAnyAuthority("SCOPE_BUSINESS")
                                .antMatchers(HttpMethod.POST, "/campaign/*/reward/*/join")
                                .hasAnyAuthority("SCOPE_CUSTOMER")

                                // RewardType API
                                .antMatchers(HttpMethod.GET, "/rewardType").permitAll()


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

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}

