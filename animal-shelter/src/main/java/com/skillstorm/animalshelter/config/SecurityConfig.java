package com.skillstorm.animalshelter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oauth2LoginFailureHandler;
    private final CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
            OAuth2LoginSuccessHandler oauth2LoginSuccessHandler,
            OAuth2LoginFailureHandler oauth2LoginFailureHandler,
            CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
        this.oauth2LoginFailureHandler = oauth2LoginFailureHandler;
        this.customOAuth2AuthorizationRequestResolver = customOAuth2AuthorizationRequestResolver;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/oauth2/google-url", "/oauth2/**").permitAll()
                        .requestMatchers("/api/auth/me", "/api/auth/link-google").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/animals", "/api/animals/*", "/api/animals/*/photos").permitAll()
                        .requestMatchers("/api/adopter/**").hasRole("ADOPTER")
                        .requestMatchers("/api/staff/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/animals/*/events").hasRole("STAFF")
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.sendError(401, "Unauthorized");
                                return;
                            }
                            response.sendRedirect("/oauth2/authorization/google");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                                        .getContext()
                                        .getAuthentication();
                                boolean authenticated = authentication != null
                                        && authentication.isAuthenticated()
                                        && !"anonymousUser".equals(authentication.getPrincipal());
                                if (!authenticated) {
                                    response.sendError(401, "Unauthorized");
                                    return;
                                }
                                response.sendError(403, "Forbidden");
                                return;
                            }
                            response.sendRedirect("/");
                        }))
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authz -> authz
                                .authorizationRequestResolver(customOAuth2AuthorizationRequestResolver))
                        .successHandler(oauth2LoginSuccessHandler)
                        .failureHandler(oauth2LoginFailureHandler));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
