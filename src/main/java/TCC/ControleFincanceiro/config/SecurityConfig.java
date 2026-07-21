package TCC.ControleFincanceiro.config;

import TCC.ControleFincanceiro.service.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserServiceImpl oauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors -> cors
                        .configurationSource(
                                corsConfigurationSource()
                        )
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(
                                frame -> frame.disable()
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/auth/**",
                                "/h2-console/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(user -> user
                                .userService(
                                        oauth2UserService
                                )
                        )
                        .defaultSuccessUrl(
                                "http://localhost:5173/dashboard",
                                true
                        )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}