package com.example.shopapp.configurations;

import com.example.shopapp.filters.JwtTokenFilter;
import com.example.shopapp.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers(
                                    String.format("%s/auth/register", apiPrefix),
                                    String.format("%s/auth/login", apiPrefix),
                                    String.format("%s/auth/outbound/authentication", apiPrefix),
                                    String.format("%s/vnpay/vnpay-payment",apiPrefix)
                            ).permitAll()
                            .requestMatchers(HttpMethod.POST, String.format("%s/users/details", apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                            //category request
                            .requestMatchers(HttpMethod.GET, String.format("%s/categories/**", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.POST, String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.PUT, String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                            //role
                            .requestMatchers(HttpMethod.GET, String.format("%s/roles", apiPrefix)).permitAll()
                            //product request
                            .requestMatchers(HttpMethod.GET, String.format("%s/products/**", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.POST, String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.PUT, String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            //product image
                            .requestMatchers(HttpMethod.GET, String.format("%s/products/images/*", apiPrefix)).permitAll()
                            //order request
                            .requestMatchers(HttpMethod.POST, String.format("%s/order/**", apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(HttpMethod.GET, String.format("%s/order/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(HttpMethod.PUT, String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            //order detail request
                            .requestMatchers(HttpMethod.POST, String.format("%s/order_details/**", apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(HttpMethod.GET, String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(HttpMethod.PUT, String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                            //cart
                            .requestMatchers(HttpMethod.POST, String.format("%s/carts", apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(HttpMethod.GET, String.format("%s/carts/**", apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/carts/**", apiPrefix)).hasRole(Role.USER)
                            //vn pay
                            .requestMatchers(HttpMethod.POST, String.format("%s/vnpay/**", apiPrefix)).hasRole(Role.USER)
                            .anyRequest().authenticated();
                });
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
