package com.sinotrans.blankbill.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // 白名单路径，需与 application.yml 保持一致
    private static final String[] WHITELIST_PATHS = {
        "/api/v1/**",
        "/api/health",
        "/doc.html",
        "/webjars/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITELIST_PATHS).permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
