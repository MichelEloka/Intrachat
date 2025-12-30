package fr.intrachat.auth;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/auth/health").permitAll()
        .anyRequest().authenticated()
      )
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

    return http.build();
  }


  // gvehshfdrjh
  @Bean
  CorsConfigurationSource corsConfigurationSource(
      @Value("${app.cors.allowed-origins:*}") String allowedOrigins,
      @Value("${app.cors.allow-credentials:false}") boolean allowCredentials) {
    List<String> origins = Arrays.stream(allowedOrigins.split(","))
      .map(String::trim)
      .filter(value -> !value.isEmpty())
      .toList();

    CorsConfiguration configuration = new CorsConfiguration();
    if (origins.isEmpty() || origins.contains("*")) {
      configuration.setAllowedOriginPatterns(List.of("*"));
    } else if (origins.stream().anyMatch(origin -> origin.contains("*"))) {
      configuration.setAllowedOriginPatterns(origins);
    } else {
      configuration.setAllowedOrigins(origins);
    }
    configuration.setAllowCredentials(allowCredentials);
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of(
      "Authorization",
      "Content-Type",
      "Accept",
      "Origin",
      "X-Requested-With"
    ));
    configuration.setExposedHeaders(List.of("WWW-Authenticate"));
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
