/*
 * Copyright 2025 com.core.township
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.town.auth.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configures the application's security settings using Spring Security for a reactive (WebFlux) environment.
 * This class is responsible for defining authentication mechanisms, password encoding, authorization rules,
 * and CORS (Cross-Origin Resource Sharing) policies.
 */
@Configuration
@EnableWebFluxSecurity
public class TownSecurityConfig {

  /**
   * Creates a {@link PasswordEncoder} bean that uses the BCrypt strong hashing function.
   * This is the standard for securely storing user passwords.
   *
   * @return A {@link BCryptPasswordEncoder} instance.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Creates a {@link ReactiveAuthenticationManager} bean responsible for authenticating users.
   * It uses a {@link ReactiveUserDetailsService} to fetch user details and a {@link PasswordEncoder}
   * to verify the provided password.
   *
   * @param userDetailsService The service to load user-specific data.
   * @param passwordEncoder    The encoder to validate passwords.
   * @return A configured {@link ReactiveAuthenticationManager} instance.
   */
  @Bean
  public ReactiveAuthenticationManager reactiveAuthenticationManager(
      ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    var authManager =
            new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    authManager.setPasswordEncoder(passwordEncoder);
    return authManager;
  }

  /**
   * Configures the main security filter chain that protects the application's endpoints.
   * This method defines which paths are public, which require authentication, and which require specific roles.
   * It also disables CSRF, form login, and HTTP Basic authentication in favor of a custom token-based approach.
   *
   * @param http                        The {@link ServerHttpSecurity} to configure.
   * @param authManager                 The authentication manager to process authentication requests.
   * @param townSecurityContextRepository The custom repository to manage the security context.
   * @param corsConfigurationSource     The source for CORS configuration.
   * @return A configured {@link SecurityWebFilterChain} instance.
   */
  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http,
      ReactiveAuthenticationManager authManager,
      TownSecurityContextRepository townSecurityContextRepository,
      CorsConfigurationSource corsConfigurationSource
  ) {
    return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authenticationManager(authManager)
            .securityContextRepository(townSecurityContextRepository)
            .authorizeExchange(exchanges -> exchanges
                    .pathMatchers(HttpMethod.POST, TownSecurityConstants.USER_REGISTRATION_PATH).permitAll()
                    .pathMatchers(HttpMethod.GET, TownSecurityConstants.HEALTH_CHECK_PATH).permitAll()
                    .pathMatchers(TownSecurityConstants.AUTH_PATH_PATTERN).permitAll()
                    .pathMatchers(HttpMethod.GET, TownSecurityConstants.PUBLIC_API_PATH_PATTERN).permitAll()
                    .pathMatchers(TownSecurityConstants.ADMIN_BUILDING_PATH_PATTERN).hasRole(TownSecurityConstants.ROLE_BUILDING_ADMIN)
                    .pathMatchers(TownSecurityConstants.ADMIN_SOCIETY_PATH_PATTERN).hasRole(TownSecurityConstants.ROLE_SOCIETY_ADMIN)
                    .anyExchange().authenticated()
            )
            .build();
  }

  /**
   * Configures the CORS (Cross-Origin Resource Sharing) policy for the application.
   * This is crucial for cookie-based authentication with a separate frontend, as it allows
   * the browser to send credentials (like cookies) to the backend.
   *
   * @return A {@link CorsConfigurationSource} bean that applies the defined CORS rules.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    var configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(TownSecurityConstants.ALLOWED_ORIGIN));
    configuration.setAllowedMethods(TownSecurityConstants.ALLOWED_METHODS);
    configuration.setAllowedHeaders(TownSecurityConstants.ALLOWED_HEADERS);
    configuration.setAllowCredentials(true);

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(TownSecurityConstants.CORS_CONFIG_PATH_PATTERN, configuration);
    return source;
  }
}