package de.cronos.demo.mapping;

import de.cronos.demo.mapping.common.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // TODO: Externalize IDM
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.builder().username("user")
                .password("{noop}test123")
                .roles(AppConstants.ROLE_NAME_USER)
                .build());
        manager.createUser(User.builder()
                .username("admin")
                .password("{noop}test123")
                .roles(AppConstants.ROLE_NAME_ADMIN)
                .build());
        return manager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .regexMatchers(HttpMethod.GET, "/b2c/statistics/?.*").permitAll()
                        .regexMatchers(HttpMethod.GET, "/b2c/customers/?.*").hasRole(AppConstants.ROLE_NAME_ADMIN)
                        .anyRequest().authenticated()
                )
                .httpBasic(basicConfigurer -> basicConfigurer.realmName("Demo: Mapping with MapStruct"));
        return http.build();
    }

}
