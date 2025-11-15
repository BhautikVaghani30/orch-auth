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

package com.core.township.user.controller;

import com.core.township.common.enums.CoreApiStatus;
import com.core.township.common.exception.model.CoreServiceError;
import com.core.township.common.model.response.CoreMessage;
import com.core.township.common.model.response.CoreMeta;
import com.core.township.common.model.response.CoreResponse;
import com.core.township.common.utils.CoreJsonParserUtils;
import com.core.township.user.constants.ValidationMessages;
import com.core.township.user.dto.LoginRequest;
import com.core.township.user.exception.CoreServiceException;
import com.core.township.user.model.api.TownAuthUserApi;
import com.core.township.user.service.TownAuthService; // Import the service
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie; // Import for reactive cookie setting
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
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
  private final ServerSecurityContextRepository securityContextRepository;

  private static final String AUTH_COOKIE_NAME = "Township-Auth";

  /**
   * Handles the user login request. It delegates authentication/token generation to the service,
   * then sets the generated JWT into a secure HTTP cookie.
   *
   * @param loginRequestMono A Mono containing the user's login credentials (email and password).
   * @param exchange The current server web exchange, used to manage the session and response.
   * @return A {@code Mono<CoreResponse<?>>} with success/error details.
   */
  @Override
  public Mono<CoreResponse> login(
      @Valid @RequestBody Mono<LoginRequest> loginRequestMono, ServerWebExchange exchange) {

    return loginRequestMono
        .switchIfEmpty(
            Mono.error(() -> new CoreServiceException(ValidationMessages.CREDENTIALS_REQUIRED)))
        .flatMap(
            request -> {
              log.debug(
                  "TownAuthController : login :: Login attempt for user: {}", request.getEmail());

              return authService
                  .authenticateAndGenerateToken(request, exchange)
                  .map(jwtToken -> buildSuccessResponse())
                  .doOnSuccess(
                      response ->
                          log.debug(
                              "TownAuthController : login :: User {} logged in successfully. Cookie set.",
                              request.getEmail()));
            })
        .onErrorResume(
            exception -> {
              if (exception instanceof CoreServiceException coreException) {
                log.warn(
                    "TownAuthController : login :: Login failed with business error: {}",
                    coreException.getMessage());
                String errorCode =
                    Optional.of(coreException)
                        .map(CoreServiceException::getError)
                        .map(CoreServiceError::getCode)
                        .orElse(ValidationMessages.TECHNICAL_ERROR);
                return Mono.error(new CoreServiceException(errorCode));
              }
              log.error(
                  "TownAuthController : login :: An unexpected error occurred during login : ",
                  exception);
              return Mono.error(exception);
            })
        .doOnSuccess(
            response ->
                log.debug(
                    "TownAuthController : login :: Login process completed successfully. Response meta: {}",
                    CoreJsonParserUtils.getJsonFromObject(response)));
  }

  /**
   * Handles the user logout request by clearing the security context and deleting the cookie.
   *
   * @param exchange The current server web exchange.
   * @return A {@code Mono<Void>} that completes once the session is cleared.
   */
  @Override
  public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {
    log.info("TownAuthController : logout :: User logout initiated, clearing session and cookie.");

    return securityContextRepository
        .save(exchange, null)
        .then(Mono.fromRunnable(() -> clearAuthCookie(exchange)))
        .then(Mono.just(ResponseEntity.noContent().build()));
  }

  /**
   * Clears the authentication cookie by setting its max-age to 0.
   *
   * @param exchange The current server web exchange.
   */
  private void clearAuthCookie(ServerWebExchange exchange) {
    log.debug("TownAuthController :: clearAuthCookie : Clearing authentication cookie.");
    ResponseCookie expiredCookie =
        ResponseCookie.from(AUTH_COOKIE_NAME, "")
            .maxAge(0)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .build();
    exchange.getResponse().getHeaders().add(HttpHeaders.SET_COOKIE, expiredCookie.toString());
  }

  /**
   * Builds a standardized success CoreResponse for the API.
   *
   * @return A {@code CoreResponse<?>>} representing the successful response.
   */
  private CoreResponse buildSuccessResponse() {
    CoreMessage successMessage =
        CoreMessage.builder()
            .code(ValidationMessages.AUTHENTICATED)
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

  // ... (getMe method remains unchanged)
  @Override
  public Mono<ResponseEntity<Authentication>> getMe(Mono<Authentication> authenticationMono) {
    return authenticationMono
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
        .doOnSuccess(
            response ->
                log.debug(
                    "TownAuthController : getMe :: getMe request processed with status: {}",
                    response.getStatusCode()));
  }
}
