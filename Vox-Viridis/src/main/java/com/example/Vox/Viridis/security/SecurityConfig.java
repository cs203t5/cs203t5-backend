package com.example.Vox.Viridis.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;
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
                                .hasAnyAuthority("BUSINESS", "ROLE_BUSINESS")
                                .antMatchers(HttpMethod.PUT, "/campaign/*")
                                .hasAnyAuthority("BUSINESS", "ROLE_BUSINESS")
                                .antMatchers(HttpMethod.DELETE, "/campaign/*")
                                .hasAnyAuthority("BUSINESS", "ROLE_BUSINESS")
                                .antMatchers(HttpMethod.GET, "/campaign/*").permitAll()
                                .antMatchers(HttpMethod.GET, "/campaign").permitAll()
                                .antMatchers(HttpMethod.GET, "/campaign/myCampaign")
                                .hasAnyAuthority("BUSINESS", "ROLE_BUSINESS")

                                // users API
                                .antMatchers(HttpMethod.PUT, "/users/role/**")
                                .hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.GET, "/users")
                                .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                                // reward API
                                .antMatchers(HttpMethod.GET, "/reward", "/reward/*",
                                                "/reward/byCampaign/*")
                                .permitAll().antMatchers(HttpMethod.POST, "/reward/*")
                                .hasAnyAuthority("BUSINESS", "ROLE_BUSINESS")
                                .antMatchers(HttpMethod.PUT, "/reward/*")
                                .hasAnyAuthority("BUSINESS", "ROLE_BUSINESS")
                                .antMatchers(HttpMethod.DELETE, "/reward/*")
                                .hasAnyAuthority("BUSINESS", "ROLE_BUSINESS")
                                .antMatchers(HttpMethod.POST, "/reward/*/join")
                                .hasAnyAuthority("CONSUMER", "ROLE_CONSUMER")

                                // RewardType API
                                .antMatchers(HttpMethod.GET, "/rewardType").permitAll()

                                // Participation API
                                .antMatchers(HttpMethod.GET, "/participation",
                                                "/participation/myPoints")
                                .hasAnyAuthority("CONSUMER", "ROLE_CONSUMER")
                                .antMatchers(HttpMethod.POST, "/participation/*")
                                .hasAnyAuthority("CONSUMER", "ROLE_CONSUMER")
                                .antMatchers(HttpMethod.POST, "/participation/addPoints/*")
                                .hasAnyAuthority("BUSINESS", "ROLE_BUSINESS")

                                // product API
                                .antMatchers(HttpMethod.GET, "/products").permitAll()
                                .antMatchers(HttpMethod.GET, "/products/*").permitAll()
                                .antMatchers(HttpMethod.POST, "/products")
                                .hasAnyAuthority( "ADMIN","ROLE_ADMIN")
                                .antMatchers(HttpMethod.PUT, "/products/*")
                                .hasAnyAuthority("ADMIN","ROLE_ADMIN")
                                .antMatchers(HttpMethod.DELETE, "/products/*")
                                .hasAnyAuthority("ADMIN","ROLE_ADMIN")
                                .antMatchers(HttpMethod.POST, "/products/buy/*").hasAnyAuthority("CONSUMER","ROLE_CONSUMER")

                                // Role API
                                .antMatchers(HttpMethod.GET, "/role")
                                .hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.GET, "/role/*")
                                .hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.POST, "/role")
                                .hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.PUT, "/role/*")
                                .hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                                .antMatchers(HttpMethod.DELETE, "/role/*")
                                .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                                .antMatchers(HttpMethod.OPTIONS).permitAll().anyRequest()
                                .authenticated()).csrf(csrf -> csrf.disable())
                                .httpBasic(Customizer.withDefaults())
                                .userDetailsService(myUserDetailsService)
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                                                jwtAuthenticationConverter())))
                                .sessionManagement((session) -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS));
                return http.build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
                                new JwtGrantedAuthoritiesConverter();
                grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

                JwtAuthenticationConverter jwtAuthenticationConverter =
                                new JwtAuthenticationConverter();
                jwtAuthenticationConverter
                                .setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
                return jwtAuthenticationConverter;
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
                configuration.setAllowedMethods(
                                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }


}

