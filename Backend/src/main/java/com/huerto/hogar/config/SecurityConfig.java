package com.huerto.hogar.config;

import com.huerto.hogar.security.filter.JwtAuthFilter;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors().configurationSource(corsSource()).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
                // Docs
                .antMatchers("/swagger-ui/**","/api-docs/**",
                             "/swagger-ui.html","/v3/api-docs/**").permitAll()
                // Auth
                .antMatchers("/api/auth/**").permitAll()
                // Tienda pública (GET)
                .antMatchers(HttpMethod.GET,
                    "/api/v1/entities/productos/**",
                    "/api/v1/entities/categorias/**",
                    "/api/regiones/**").permitAll()
                // Validar cupón: público
                .antMatchers(HttpMethod.GET,"/api/v1/entities/cupones/validar/**").permitAll()
                // Perfil usuario
                .antMatchers("/api/usuarios/perfil/**").authenticated()
                // Carrito y pedidos propios: autenticado
                .antMatchers("/api/v1/entities/carrito/**").authenticated()
                .antMatchers("/api/v1/entities/pedidos/checkout").authenticated()
                .antMatchers("/api/v1/entities/pedidos/mis-pedidos/**").authenticated()
                // Admin exclusivo
                .antMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.POST,"/api/v1/entities/productos/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.PUT,"/api/v1/entities/productos/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.DELETE,"/api/v1/entities/productos/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.POST,"/api/v1/entities/categorias/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.PUT,"/api/v1/entities/categorias/**").hasRole("ADMINISTRADOR")
                .antMatchers(HttpMethod.DELETE,"/api/v1/entities/categorias/**").hasRole("ADMINISTRADOR")
                // Admin y vendedor: ver pedidos
                .antMatchers(HttpMethod.GET,"/api/v1/entities/pedidos/**")
                    .hasAnyRole("ADMINISTRADOR","VENDEDOR")
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info().title("Huerto Hogar API").version("1.0")
                .description("API REST Huerto Hogar — Spring Boot 2.7 + JWT"))
            .components(new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                    .scheme("bearer").bearerFormat("JWT")));
    }
}
