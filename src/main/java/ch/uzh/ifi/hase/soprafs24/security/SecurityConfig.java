package ch.uzh.ifi.hase.soprafs24.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Use this CORS configuration
            .cors().and()
            // Disable CSRF for RESTful API
            .csrf().disable()
            // Use stateless session (no session cookies)
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // Public endpoints - no authentication needed
                .antMatchers("/users/register", "/login", "/courses", "/chat/**", "/users/me").permitAll()
                // OPTIONS requests are always permitted for CORS preflight
                .antMatchers("OPTIONS", "/**").permitAll()
                // All other requests need authentication
                .anyRequest().authenticated();
                
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "https://sopra-fs25-group-38-client.vercel.app"
        ));
        
        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        
        // Set allowed headers
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        
        // Expose Authorization header to frontend
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Don't allow credentials (cookies) since we use token auth
        configuration.setAllowCredentials(false);
        
        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);
        
        // Apply this configuration to all paths
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

