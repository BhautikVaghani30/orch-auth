/*
 * Copyright 2025 com.core.township
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.core.township.user.service;

import com.core.township.common.utils.CoreJsonParserUtils;
import com.core.township.user.constants.ValidationMessages;
import com.core.township.user.dto.LoginRequest;
import com.core.township.user.exception.CoreServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Service responsible for orchestrating the user authentication and session management flow. It
 * interacts with Spring Security's reactive components to handle login and logout.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TownAuthService {

  private final ReactiveAuthenticationManager authenticationManager;
  private final ServerSecurityContextRepository securityContextRepository;

  /**
   * Authenticates the user, establishes a session, and generates a JWT.
   *
   * @param request The user's login credentials.
   * @param exchange The current server web exchange for session management.
   * @return A {@code Mono<String>} that completes with the generated JWT token.
   */
  public Mono<Authentication> authenticateAndGenerateToken(
      LoginRequest request, ServerWebExchange exchange) {

    log.debug(
        "TownAuthService : authenticateAndGenerateToken :: Attempting authentication for user: {}",
        CoreJsonParserUtils.getJsonFromObject(request));

    Authentication authenticationToken =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

    return authenticationManager
        .authenticate(authenticationToken)
        .flatMap(
            authentication -> {
              log.debug(
                  "TownAuthService : authenticateAndGenerateToken :: Authentication successful for user: {}, other details: {}",
                  authentication.getName(),
                  CoreJsonParserUtils.getJsonFromObject(authentication.getDetails()));
              SecurityContextImpl context = new SecurityContextImpl(authentication);
              return securityContextRepository.save(exchange, context).thenReturn(authentication);
            })
        .onErrorResume(
            AuthenticationException.class,
            exception -> {
              log.warn(
                  "TownAuthService : authenticateAndGenerateToken :: Authentication failed for user: {}",
                  request.getEmail());
              log.debug(
                  "TownAuthService : authenticateAndGenerateToken :: Authentication failure details: ",
                  exception);
              return Mono.error(new CoreServiceException(ValidationMessages.UNAUTHORIZED));
            });
  }
}
