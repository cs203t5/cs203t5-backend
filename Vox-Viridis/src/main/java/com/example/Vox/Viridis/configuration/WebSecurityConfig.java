package com.example.Vox.Viridis.configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//added this on 16/10
public class WebSecurityConfig extends com.example.Vox.Viridis.security.WebSecurityConfig{
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
        .antMatchers("/api/ws/**",
        "/ws/**"
        )
        .permitAll();
         }
}
