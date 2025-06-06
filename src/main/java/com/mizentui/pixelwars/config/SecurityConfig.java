package com.mizentui.pixelwars.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        httpSecurity
                .authorizeHttpRequests(matcherRegistry -> matcherRegistry.requestMatchers("/", "/error", "/webjars/**", "/favicon.ico", "/js/index.js", "/css/style.css", "/canvas/**").permitAll().anyRequest().authenticated()).oauth2Login(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                .deleteCookies("JSESSIONID", "remember-me").clearAuthentication(true).invalidateHttpSession(true).permitAll());
        return httpSecurity.build();
    }

    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
        return oidcLogoutSuccessHandler;
    }
}
