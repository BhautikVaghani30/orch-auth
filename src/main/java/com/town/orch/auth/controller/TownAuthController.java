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

package com.town.orch.auth.controller;

import com.town.common.enums.CoreApiStatus;
import com.town.common.exception.model.CoreServiceError;
import com.town.common.model.response.CoreMessage;
import com.town.common.model.response.CoreMeta;
import com.town.common.model.response.CoreResponse;
import com.town.common.utils.CoreJsonParserUtils;
import com.town.orch.auth.config.security.TownAuthSecurityConstants;
import com.town.orch.auth.constants.TownErrorMessages;
import com.town.orch.auth.dto.LoginRequest;
import com.town.orch.auth.exception.CoreServiceException;
import com.town.orch.auth.model.api.TownAuthUserApi;
import com.town.orch.auth.service.TownAuthService;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Controller responsible for handling user authentication endpoints such as login, logout, and
 * retrieving the current user's status. It orchestrates the authentication flow using Spring
 * Security's reactive components, delegating core logic to the {@link TownAuthService}.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TownAuthController implements TownAuthUserApi {

  private final TownAuthService authService;

  /**
   * Orchestrates the user login process within a reactive pipeline. This method validates the
   * incoming {@link LoginRequest}, delegates the core authentication and JWT generation logic to
   * the {@link TownAuthService}, and upon successful authentication, sets a secure, HTTP-only
   * cookie on the response. The entire flow is asynchronous and includes robust error handling to
   * differentiate between business-related exceptions (e.g., invalid credentials) and unexpected
   * technical errors, ensuring a standardized {@link CoreResponse} is always returned.
   *
   * @param loginRequestMono A Mono containing the user's login credentials (email and password).
   * @param exchange The current server web exchange, used to modify the response with the auth
   *     cookie.
   * @return A {@code Mono<CoreResponse<?>>} with success/error details.
   */
  @Override
  public Mono<CoreResponse> login(
      @Valid @RequestBody Mono<LoginRequest> loginRequestMono, ServerWebExchange exchange) {

    return loginRequestMono
        .switchIfEmpty(
            Mono.error(() -> new CoreServiceException(TownErrorMessages.CREDENTIALS_REQUIRED)))
        .flatMap(
            request -> {
              log.debug(
                  "TownAuthController :: login : Login attempt for user: {}", request.getEmail());

              return authService
                  .authenticateAndGenerateToken(request)
                  .map(
                      jwtToken -> {
                        setAuthCookie(exchange, jwtToken);
                        return buildSuccessResponse();
                      })
                  .doOnSuccess(
                      response ->
                          log.debug(
                              "TownAuthController :: login : User {} logged in successfully. Cookie set.",
                              request.getEmail()));
            })
        .onErrorResume(
            exception -> {
              if (exception instanceof CoreServiceException coreException) {
                log.warn(
                    "TownAuthController :: login : Login failed with business error: {}",
                    coreException.getMessage());
                String errorCode =
                    Optional.of(coreException)
                        .map(CoreServiceException::getError)
                        .map(CoreServiceError::getCode)
                        .orElse(TownErrorMessages.TECHNICAL_ERROR);
                return Mono.error(new CoreServiceException(errorCode));
              }
              log.error(
                  "TownAuthController :: login : An unexpected error occurred during login : ",
                  exception);
              return Mono.error(exception);
            })
        .doOnSuccess(
            response ->
                log.debug(
                    "TownAuthController :: login : Login process completed successfully. Response meta: {}",
                    CoreJsonParserUtils.getJsonFromObject(response)));
  }

  /**
   * Handles the user logout request by clearing the authentication cookie. In a stateless,
   * token-based architecture, logging out is a client-side concern. This endpoint facilitates it by
   * instructing the browser to delete the auth cookie. It returns a {@link Mono} that completes
   * with an HTTP 204 No Content status, signaling successful cookie clearance.
   *
   * @param exchange The current server web exchange.
   * @return A {@code Mono<ResponseEntity<Void>>} that completes once the cookie is cleared.
   */
  @Override
  public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {
    log.info("TownAuthController :: logout : User logout initiated, clearing cookie.");
    return Mono.fromRunnable(() -> clearAuthCookie(exchange))
        .then(Mono.just(ResponseEntity.noContent().build()));
  }

  /**
   * A private helper method that constructs an expired {@link ResponseCookie} and adds it to the
   * response headers. By setting the cookie's {@code maxAge} to 0, it effectively instructs the
   * browser to delete the cookie, thus clearing the user's session token from the client.
   *
   * @param exchange The current server web exchange.
   */
  private void clearAuthCookie(ServerWebExchange exchange) {
    log.debug("TownAuthController :: clearAuthCookie : Clearing authentication cookie.");
    ResponseCookie expiredCookie =
        ResponseCookie.from(TownAuthSecurityConstants.AUTH_COOKIE_NAME, "")
            .maxAge(0)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .build();
    exchange.getResponse().getHeaders().add(HttpHeaders.SET_COOKIE, expiredCookie.toString());
  }

  /**
   * A private factory method that constructs a standardized {@link CoreResponse} for successful
   * operations. It encapsulates the creation of success metadata and messages, ensuring a
   * consistent and predictable response structure for clients.
   *
   * @return A {@code CoreResponse<?>>} representing the successful response.
   */
  private CoreResponse buildSuccessResponse() {
    CoreMessage successMessage =
        CoreMessage.builder()
            .code(TownErrorMessages.AUTHENTICATED)
            .type(CoreMessage.CoreMessageType.INFO.toString())
            .build();

    CoreMeta meta =
        CoreMeta.builder()
            .timestamp(Instant.now().toString())
            .status(CoreApiStatus.SUCCESS.getValue())
            .build();

    return CoreResponse.create(
        coreResponseBuilder ->
            coreResponseBuilder
                .withMeta(meta)
                .withData(Collections.emptyList()) // Use Collections.emptyList() for cleanliness
                .withMessages(Collections.singletonList(successMessage)));
  }

  /**
   * Retrieves the details of the currently authenticated user. It relies on Spring Security's
   * context to provide the {@link Authentication} object, which is then mapped into a {@link
   * ResponseEntity}. If no user is authenticated, it returns an HTTP 401 Unauthorized status.
   *
   * @param authenticationMono A {@link Mono} containing the {@link Authentication} object, injected
   *     by Spring Security.
   * @return A {@code Mono<ResponseEntity<Authentication>>} containing the user's details or an
   *     error status.
   */
  @Override
  public Mono<ResponseEntity<Authentication>> getMe(Mono<Authentication> authenticationMono) {
    return authenticationMono
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
        .doOnSuccess(
            response ->
                log.debug(
                    "TownAuthController :: getMe : getMe request processed with status: {}",
                    response.getStatusCode()));
  }

  /**
   * A private helper method to create and set a secure, HTTP-only authentication cookie on the HTTP
   * response. The cookie contains the JWT and is configured with security best practices, such as
   * the {@code secure} and {@code httpOnly} flags, and a max age that should align with the token's
   * expiration time.
   *
   * @param exchange The current server web exchange.
   * @param token The JWT to be stored in the cookie.
   */
  private void setAuthCookie(ServerWebExchange exchange, String token) {
    log.debug("TownAuthController :: setAuthCookie : Setting authentication cookie.");
    ResponseCookie authCookie =
        ResponseCookie.from(TownAuthSecurityConstants.AUTH_COOKIE_NAME, token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofHours(10))
            .build();
    exchange.getResponse().getHeaders().add(HttpHeaders.SET_COOKIE, authCookie.toString());
  }
}
