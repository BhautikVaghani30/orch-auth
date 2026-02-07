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

package com.town.orch.auth.config.security;

import com.town.orch.auth.util.TownJwtConstants;
import com.town.orch.auth.util.TownJwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * A custom {@link ServerSecurityContextRepository} that implements stateless, JWT-based
 * authentication. It extracts a JWT from an HTTP-only cookie, validates it, and populates the
 * {@link SecurityContext} for each request.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TownAuthSecurityContextRepository implements ServerSecurityContextRepository {

  private final TownJwtUtil jwtUtil;

  /**
   * In a stateless JWT-based architecture, the security context is not persisted across requests.
   * Therefore, this method is a no-op and simply returns an empty {@code Mono} to fulfill the
   * contract of the {@link ServerSecurityContextRepository} interface.
   *
   * @param exchange The server web exchange.
   * @param context The security context.
   * @return An empty Mono to signify completion.
   */
  @Override
  public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
    // Stateless: No need to save anything.
    return Mono.empty();
  }

  /**
   * Loads the {@link SecurityContext} by orchestrating the extraction and validation of a JWT from
   * the request's cookie. This method initiates the reactive chain by attempting to find the token,
   * and if successful, delegates to another method to build the security context. If the token is
   * invalid or not present, the chain completes with an empty {@code Mono}, indicating no
   * authenticated user.
   *
   * @param exchange The current server web exchange.
   * @return A {@code Mono<SecurityContext>} containing the authenticated user's context, or an empty Mono.
   */
  @Override
  public Mono<SecurityContext> load(ServerWebExchange exchange) {
    return extractTokenFromCookie(exchange)
        .flatMap(this::createSecurityContextFromToken)
        .doOnNext(
            context ->
                log.debug(
                    "TownSecurityContextRepository :: load : Security context loaded successfully for user: {}",
                    context.getAuthentication().getName()))
        .doOnError(
            ex ->
                log.warn(
                    "TownSecurityContextRepository :: load : Failed to load security context from token.",
                    ex));
  }

  /**
   * A private helper method that searches for the authentication cookie (defined by
   * {@link TownAuthSecurityConstants#AUTH_COOKIE_NAME}) within the request. If the cookie is found, it
   * extracts the raw JWT string and emits it as a {@code Mono<String>}. If not found, it returns an
   * empty {@code Mono}.
   *
   * @param exchange The current server web exchange.
   * @return A {@code Mono} emitting the token string, or an empty Mono if the cookie is absent.
   */
  private Mono<String> extractTokenFromCookie(ServerWebExchange exchange) {
    return Mono.justOrEmpty(
        exchange.getRequest().getCookies().getFirst(TownAuthSecurityConstants.AUTH_COOKIE_NAME))
        .map(HttpCookie::getValue);
  }

  /**
   * Creates a {@link SecurityContext} from a given JWT string. This method validates the token using
   * the {@link TownJwtUtil}, extracts the username and role claims, and constructs a fully formed
   * {@link Authentication} object. This object is then wrapped in a {@link SecurityContextImpl} and
   * returned within a {@code Mono}.
   *
   * @param token The raw JWT string extracted from the cookie.
   * @return A {@code Mono<SecurityContext>} containing the populated security context.
   */
  private Mono<SecurityContext> createSecurityContextFromToken(String token) {
    Mono<String> usernameMono = Mono.justOrEmpty(jwtUtil.extractUsername(token));
    Mono<String> roleMono =
        Mono.justOrEmpty(
            jwtUtil.extractClaim(token, claims -> claims.get(TownJwtConstants.ROLE_CLAIM, String.class)));

    return usernameMono
        .zipWith(roleMono)
        .map(
            tuple -> {
              String username = tuple.getT1();
              String role = tuple.getT2();
              var authorities = java.util.List.of(new SimpleGrantedAuthority(role));

              Authentication authentication =
                  new UsernamePasswordAuthenticationToken(username, null, authorities);
              return new SecurityContextImpl(authentication);
            })
        .cast(SecurityContext.class);
  }
}
