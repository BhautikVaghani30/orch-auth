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

package com.town.orch.auth.service;

import com.town.orch.auth.config.security.TownshipAuthUserDetails;
import com.town.orch.auth.constants.TownErrorMessages;
import com.town.orch.auth.dto.LoginRequest;
import com.town.orch.auth.exception.CoreServiceException;
import com.town.orch.auth.util.TownJwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

/**
 * Service responsible for orchestrating the user authentication and session management flow. It
 * interacts with Spring Security's reactive components to handle login and logout. This service is
 * designed to be stateless, focusing solely on the logic of authentication and token generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TownAuthService {

  private final ReactiveAuthenticationManager authenticationManager;
  private final TownJwtUtil townJwtUtil;

  /**
   * Authenticates a user based on the provided credentials and generates a JWT upon success. This
   * method orchestrates the authentication flow by delegating to the {@link
   * ReactiveAuthenticationManager} and then uses the resulting {@link
   * org.springframework.security.core.Authentication} object to create a token.
   *
   * @param request The user's login credentials.
   * @return A {@code Mono<String>} that emits the generated JWT. If authentication fails, the Mono
   *     will error with a {@link CoreServiceException}.
   */
  public Mono<String> authenticateAndGenerateToken(LoginRequest request) {

    log.debug(
        "TownAuthService :: authenticateAndGenerateToken : Attempting authentication for user: {}",
        request.getEmail());

    var authenticationToken =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

    return authenticationManager
        .authenticate(authenticationToken)
        .map(this::generateTokenForAuthentication)
        .doOnSuccess(
            token ->
                log.info(
                    "TownAuthService :: authenticateAndGenerateToken : JWT generated successfully for user: {}",
                    request.getEmail()))
        .onErrorMap(
            AuthenticationException.class,
            ex -> {
              log.warn(
                  "TownAuthService :: authenticateAndGenerateToken : Authentication failed for user: {}. Reason: {}",
                  request.getEmail(),
                  ex.getMessage());
              return new CoreServiceException(TownErrorMessages.UNAUTHORIZED);
            });
  }

  /**
   * A private helper method to generate a JWT from a successful {@link
   * org.springframework.security.core.Authentication} object.
   *
   * @param authentication The authenticated user principal.
   * @return The generated JWT string.
   */
  private String generateTokenForAuthentication(
      org.springframework.security.core.Authentication authentication) {
    var userDetails = (TownshipAuthUserDetails) authentication.getPrincipal();
    return townJwtUtil.generateToken(
        userDetails.getUsername(),
        userDetails.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .orElse("ROLE_ANONYMOUS"),
        UUID.randomUUID().toString(),
        Optional.of(userDetails).map(TownshipAuthUserDetails::getUserId).orElse(null));
  }
}
