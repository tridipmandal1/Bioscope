package com.bioscope.backend.v01.configs;

import com.bioscope.backend.v01.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurations {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfigurations( AuthenticationProvider authenticationProvider,
                                   JwtAuthFilter jwtAuthFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers(
                    "/v01/auth/update",
                    "/v01/auth/change-password",
                    "v01/auth/delete").authenticated();
            authorize.requestMatchers("/v01/auth/**", "/test","/").permitAll();
            authorize.requestMatchers(
                    "/v01/user/shows",
                    "/v01/user/hosts",
                    "/v01/user/trending-shows",
                    "/v01/user/trending-movies",
                    "/v01/user/streaming/movies",
                    "/v01/user/search",
                    "/v01/user/shows/seating",
                    "/v01/user/movie/**",
                    "/v01/user/show/**",
                    "/v01/host/movie/**"
            ).permitAll();
            authorize.requestMatchers("/v01/user/**")
                   .hasAuthority("USER");
            authorize.requestMatchers("/v01/host/**").hasAuthority("HOST");
            authorize.anyRequest().authenticated();
        });
        http.authenticationProvider(authenticationProvider);
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.cors(corsConfig ->
                corsConfig.configurationSource(corsConfigurationSource()));
        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
