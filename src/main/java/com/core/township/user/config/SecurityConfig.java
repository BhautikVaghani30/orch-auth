/*
 * Copyright 2025 com.core.township
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.core.township.user.config;

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
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

/** Configures the application's security settings using Spring Security WebFlux. */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  /**
   * Defines the password encoder bean, using BCrypt for strong, one-way password hashing.
   *
   * @return The configured {@link PasswordEncoder}.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Creates the ReactiveAuthenticationManager.
   *
   * <p>It uses the injected {@link ReactiveUserDetailsService} and {@link PasswordEncoder} to
   * validate user credentials reactively.
   *
   * @param userDetailsService The service to load user details.
   * @param passwordEncoder The configured password encoder.
   * @return The configured {@link ReactiveAuthenticationManager}.
   */
  @Bean
  public ReactiveAuthenticationManager reactiveAuthenticationManager(
          ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    UserDetailsRepositoryReactiveAuthenticationManager authManager =
            new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    authManager.setPasswordEncoder(passwordEncoder);
    return authManager;
  }

  /**
   * Defines the repository for persisting the security context.
   *
   * <p>Using {@link WebSessionServerSecurityContextRepository} ensures that the authentication
   * context is stored in the WebSession, maintaining session state across requests.
   *
   * @return The configured {@link ServerSecurityContextRepository}.
   */
  @Bean
  public ServerSecurityContextRepository securityContextRepository() {
    return new WebSessionServerSecurityContextRepository();
  }

  /**
   * Configures the main security filter chain for the application.
   *
   * @param http The {@link ServerHttpSecurity} object for configuration.
   * @param authManager The injected {@link ReactiveAuthenticationManager}.
   * @param contextRepository The injected {@link ServerSecurityContextRepository}.
   * @return The built {@link SecurityWebFilterChain}.
   */
  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(
          ServerHttpSecurity http,
          ReactiveAuthenticationManager authManager,
          ServerSecurityContextRepository contextRepository) {

    return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authenticationManager(authManager)
            .securityContextRepository(contextRepository)
            .authorizeExchange(
                    exchanges ->
                            exchanges
                                    .pathMatchers(HttpMethod.POST, "/api/users")
                                    .permitAll()
                                    .pathMatchers("/api/v1/township/user", "/api/auth/**")
                                    .permitAll()
                                    .anyExchange()
                                    .authenticated())
            .build();
  }
}