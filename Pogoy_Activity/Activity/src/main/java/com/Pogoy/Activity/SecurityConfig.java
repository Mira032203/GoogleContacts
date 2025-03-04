package com.Pogoy.Activity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/contacts/view", true)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/").permitAll()
                )
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/contacts/add", "/contacts/update", "/contacts/delete") // Disable CSRF for these endpoints
//                );
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
