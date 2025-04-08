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
            .csrf().ignoringAntMatchers("/users/register", "/users/login")
            .and()
            .authorizeRequests()
                .antMatchers("/users/register", "/users/login").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }
}
