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

import com.core.township.common.utils.CoreJsonParserUtils;
import com.town.orch.auth.config.security.TownshipAuthUserDetails;
import com.town.orch.auth.constants.TownErrorMessages;
import com.town.orch.auth.entities.TownUser;
import com.town.orch.auth.exception.CoreServiceException;
import com.town.orch.auth.repository.TownAuthRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Objects;

/**
 * Service responsible for loading user-specific data during authentication. It implements {@link
 * ReactiveUserDetailsService} to provide user details in a reactive manner by querying the {@link
 * TownAuthRepository}.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class TownshipUserDetailsService implements ReactiveUserDetailsService {

  private final TownAuthRepository userRepository;

  /**
   * Locates the user based on the username (email in this case).
   *
   * @param username The email address of the user.
   * @return A {@code Mono<UserDetails>} containing the user's details.
   * @throws UsernameNotFoundException if the user is not found.
   * @throws CoreServiceException if the user is found but has a technical issue (e.g., missing
   *     password).
   */
  @Override
  public Mono<UserDetails> findByUsername(String username) {
    log.debug(
        "TownshipUserDetailsService : findByUsername :: Attempting to load user details for email: {}",
        username);

    return userRepository
        .findByEmail(username)
        .switchIfEmpty(
            Mono.error(() -> new CoreServiceException(TownErrorMessages.USER_NOT_FOUND)))
        .map(this::createUserDetails)
        .doOnSuccess(
            userDetails ->
                log.debug(
                    "TownshipUserDetailsService : findByUsername :: User details loaded successfully for user: {}",
                    userDetails.getUsername()))
        .doOnError(
            exception -> {
              if (exception instanceof CoreServiceException) {
                log.debug(
                    "TownshipUserDetailsService : findByUsername :: core service exception occurred : {}, exception : ",
                    exception.getMessage(),
                    exception);
              } else {
                log.debug(
                    "TownshipUserDetailsService : findByUsername :: Unexpected exception loading user details for {}: ",
                    username,
                    exception);
              }
            });
  }

  /**
   * Helper method to convert the data model (TownUser) to Spring Security's UserDetails. Performs
   * critical validation checks during the conversion.
   *
   * @param user The {@link TownUser} entity retrieved from the repository.
   * @return The built {@link UserDetails} object.
   * @throws CoreServiceException if the user has critical data inconsistencies.
   */
  private UserDetails createUserDetails(TownUser user) {
    if (log.isDebugEnabled()) {
      log.debug(
          "TownshipUserDetailsService : createUserDetails :: TownUser loaded: {}",
          CoreJsonParserUtils.getJsonFromObject(user));
    }

    if (Objects.isNull(user.getPassword())
            || user.getPassword().isEmpty()) {
      log.error(
          "TownshipUserDetailsService : createUserDetails :: Critical Data Error: "
              + "Password field is missing or empty for user: {} - and user: {}",
          user.getEmail(),
          CoreJsonParserUtils.getJsonFromObject(user));
      throw new CoreServiceException(TownErrorMessages.TECHNICAL_ERROR);
    }

    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    return new TownshipAuthUserDetails(user.getId(), user.getEmail(), user.getPassword(), authorities);
  }
}
