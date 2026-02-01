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

package com.town.auth.service;

import com.core.township.common.utils.CoreJsonParserUtils;
import com.town.auth.constants.TownErrorMessages;
import com.town.auth.dto.TownUserDTO;
import com.town.auth.entities.TownUser;
import com.town.auth.exception.CoreServiceException;
import com.town.auth.mapper.TownUserMapper;
import com.town.auth.model.service.TownUserService;
import com.town.auth.repository.TownUserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the {@link TownUserService} for handling user-related business logic. This
 * service manages the lifecycle of a {@link TownUser}, including creation and validation, in a
 * reactive and non-blocking manner.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TownUserServiceImpl implements TownUserService {

  /** The repository for user data access. */
  private final TownUserRepository userRepository;

  /** The encoder for hashing user passwords. */
  private final PasswordEncoder passwordEncoder;

  /** The mapper for converting between User DTOs and entities. */
  private static final TownUserMapper mapper = Mappers.getMapper(TownUserMapper.class);

  /**
   * Creates a new user, hashes their password, and saves them to the database.
   *
   * @param userDTO The DTO containing the new user's information.
   * @return A {@link Mono} emitting the newly created and saved {@link TownUser}.
   * @throws CoreServiceException if a user with the same email already exists.
   */
  @Override
  public Mono<TownUser> createUser(TownUserDTO userDTO) {
    if (log.isDebugEnabled()) {
      log.debug(
          "TownUserServiceImpl : createUser :: Attempting to create a new user with email: {} and user : {}",
          Optional.ofNullable(userDTO).map(TownUserDTO::getEmail).orElse(null),
          CoreJsonParserUtils.getJsonFromObject(userDTO));
    }

    return userRepository
        .findByEmail(Optional.ofNullable(userDTO).map(TownUserDTO::getEmail).orElse(""))
        .flatMap(
            existingUser -> {
              log.debug(
                  "TownUserServiceImpl :: createUser : User with email {} already exists. Details: {}",
                  Optional.of(existingUser).map(TownUser::getEmail).orElse(null),
                  CoreJsonParserUtils.getJsonFromObject(existingUser));
              return Mono.error(new CoreServiceException(TownErrorMessages.USER_ALREADY_EXISTS));
            })
        .switchIfEmpty(Mono.defer(() -> createAndSaveUser(userDTO)))
        .cast(TownUser.class);
  }

  /**
   * A private helper method that encapsulates the final user creation logic. It generates a UUID,
   * securely hashes the password, maps the DTO to an entity, and persists it.
   *
   * @param userDTO The user data transfer object.
   * @return A {@link Mono} emitting the saved {@link TownUser}.
   */
  private Mono<TownUser> createAndSaveUser(TownUserDTO userDTO) {
    return Mono.fromCallable(() -> passwordEncoder.encode(userDTO.getPassword()))
        .map(encodedPassword -> mapper.toEntity(userDTO, encodedPassword))
        .flatMap(userRepository::save)
        .doOnSuccess(
            savedUser ->
                log.debug(
                    "TownUserServiceImpl :: createUser : Successfully created user with ID: {}",
                    Optional.ofNullable(savedUser).map(TownUser::getId).orElse(null)));
  }
}
