package com.twitter.ms.security.config;

import lombok.RequiredArgsConstructor;

import com.twitter.ms.security.oauth2.GoogleOAuth2UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final GoogleOAuth2UserService googleOAuth2UserService;

    @Value("spring.security.oauth2.default.login-page-url")
    private String loginPageUrl;

    @Value("spring.security.oauth2.default.default-success-url")
    private String defaultSuccessUrl;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurityFilterChain) throws Exception {
        // we have already the security filter chain handled by gateway
        // so basically here allow all authorize requests for other security filter (future)
        httpSecurityFilterChain.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/ms-user-service/ui/v1/auth/**", "/ms-user-service/actuator/**").permitAll() // Disable OAuth2 login for these paths
                    .anyRequest().authenticated()
        ).oauth2Login(oauth2Login ->
            oauth2Login
                .loginPage(loginPageUrl)
                .defaultSuccessUrl(defaultSuccessUrl)
                .userInfoEndpoint(customizer -> customizer.userService(googleOAuth2UserService))
        );
        return httpSecurityFilterChain.build();
    }

}
