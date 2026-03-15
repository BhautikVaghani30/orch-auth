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

package com.town.orch.auth.model.api;

import com.town.common.model.response.CoreResponse;
import com.town.orch.auth.constants.TownApiConstants;
import com.town.orch.auth.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Defines the API for user authentication and authorization within the Township ecosystem. This
 * interface outlines the contract for login, logout, and session status operations.
 */
@RequestMapping(TownApiConstants.API_BASE_URL)
@Tag(
    name = "Town User Authentication API",
    description = "API for user authentication, authorization, and session management.")
public interface TownAuthUserApi {

  /**
   * Handles the user login request. It authenticates the user's credentials and, upon success,
   * establishes a new session by saving the security context.
   *
   * @param loginRequestMono A Mono containing the user's login credentials (email and password).
   * @param exchange The current server web exchange, used to manage the session.
   * @return A {@code Mono<CoreResponse<?>>} that completes with a success message on successful
   *     login, or an error on failure.
   */
  @PostMapping(TownApiConstants.AUTH_LOGIN_PATH)
  @Operation(
      summary = "User Login",
      description = "Authenticates a user and establishes a session.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Authentication failed")
      })
  Mono<CoreResponse> login(
      @Valid @RequestBody Mono<LoginRequest> loginRequestMono, ServerWebExchange exchange);

  /**
   * Handles the user logout request by clearing the security context, which effectively invalidates
   * the current session.
   *
   * @param exchange The current server web exchange.
   * @return A {@code Mono<Void>} that completes once the session is cleared.
   */
  @PostMapping(TownApiConstants.AUTH_LOGOUT_PATH)
  @Operation(
      summary = "User Logout",
      description = "Logs out the current user and invalidates their session.")
  Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange);

  /**
   * An endpoint to retrieve the authentication details of the currently logged-in user. This is
   * useful for client-side applications to verify the user's session status.
   *
   * @param authenticationMono A Mono provided by Spring Security containing the {@link
   *     Authentication} object if the user is authenticated.
   * @return A {@code Mono<ResponseEntity<Authentication>>} containing the user's authentication
   *     details and an HTTP 200 (OK) status, or an HTTP 401 (Unauthorized) if the user is not
   *     authenticated.
   */
  @GetMapping(TownApiConstants.AUTH_ME_PATH)
  @Operation(
      summary = "Get Current User",
      description = "Retrieves the authentication details for the currently logged-in user.")
  Mono<ResponseEntity<Authentication>> getMe(Mono<Authentication> authenticationMono);
}