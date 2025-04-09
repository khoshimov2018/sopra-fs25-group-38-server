package ch.uzh.ifi.hase.soprafs24.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
                .ignoringAntMatchers("/users/register", "/login", "/courses", "/chat/**") // CSRF exempt
            .and()
            .authorizeRequests()
                .antMatchers("/users/register", "/login", "/courses","/chat/**").permitAll() // public access
                .anyRequest().authenticated();
        return http.build();
    }
}

