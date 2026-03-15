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

package com.town.orch.auth.service;

import com.town.common.utils.CoreJsonParserUtils;
import com.town.orch.auth.adaptor.TownOrchUserAdaptor;
import com.town.orch.auth.constants.TownErrorMessages;
import com.town.orch.auth.dto.TownUserDTO;
import com.town.orch.auth.dto.TownUserProfileRequestDto;
import com.town.orch.auth.dto.TownUserProfileResponseDto;
import com.town.orch.auth.entities.TownUser;
import com.town.orch.auth.exception.CoreServiceException;
import com.town.orch.auth.mapper.TownAuthMapper;
import com.town.orch.auth.model.service.TownAuthService;
import com.town.orch.auth.repository.TownAuthRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the {@link TownAuthService} for handling user-related business logic. This
 * service manages the lifecycle of a {@link TownUser}, including creation and validation, in a
 * reactive and non-blocking manner.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TownUserServiceImpl implements TownAuthService {

  /** The repository for user data access. */
  private final TownAuthRepository userRepository;

  /** The repository for user data access. */
  private final TownOrchUserAdaptor townOrchUserAdaptor;

  /** The encoder for hashing user passwords. */
  private final PasswordEncoder passwordEncoder;

  /** The mapper for converting between User DTOs and entities. */
  private static final TownAuthMapper mapper = Mappers.getMapper(TownAuthMapper.class);

  /**
   * Creates a new user, hashes their password, and saves them to the database.
   *
   * @param userDTO The DTO containing the new user's information.
   * @return A {@link Mono} emitting the newly created and saved {@link TownUser}.
   * @throws CoreServiceException if a user with the same email already exists.
   */
  @Override
  public Mono<TownUserProfileResponseDto> createUserProfile(TownUserDTO userDTO) {
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
        .cast(TownUserProfileResponseDto.class);
  }

  /**
   * A private helper method that encapsulates the final user creation logic. It generates a UUID,
   * securely hashes the password, maps the DTO to an entity, and persists it.
   *
   * @param userDTO The user data transfer object.
   * @return A {@link Mono} emitting the saved {@link TownUser}.
   */
  private Mono<TownUserProfileResponseDto> createAndSaveUser(TownUserDTO userDTO) {
    return Mono.fromCallable(() -> passwordEncoder.encode(userDTO.getPassword()))
        .map(encodedPassword -> mapper.toEntity(userDTO, encodedPassword))
        .flatMap(
            townUser ->
                userRepository
                    .save(townUser)
                    .flatMap(
                        savedUser -> {
                          TownUserProfileRequestDto townUserProfileRequestDto =
                              TownUserProfileRequestDto.builder()
                                  .id(savedUser.getId())
                                  .email(savedUser.getEmail())
                                  .firstName(userDTO.getFirstName())
                                  .lastName(userDTO.getLastName())
                                  .phone(userDTO.getPhone())
                                  .build();
                          return townOrchUserAdaptor
                              .createUserProfile(townUserProfileRequestDto)
                              .flatMap(
                                  townUserProfileCoreResponse -> {
                                    if (log.isDebugEnabled()) {
                                      log.debug(
                                          "TownUserServiceImpl : createAndSaveUser :: town user profile creation response : {} and user credentials : {}",
                                          CoreJsonParserUtils.getJsonFromObject(
                                              townUserProfileCoreResponse),
                                          CoreJsonParserUtils.getJsonFromObject(townUser));
                                    }
                                    return Mono.just(townUserProfileCoreResponse.getData());
                                  });
                        }))
        .doOnSuccess(
            townUserProfileCoreResponse ->
                log.debug(
                    "TownUserServiceImpl :: createUser : Successfully created user with ID: {}",
                    Optional.ofNullable(townUserProfileCoreResponse)
                        .map(TownUserProfileResponseDto::getId)
                        .orElse(null)))
        .doOnError(
            error ->
                log.error(
                    "TownUserServiceImpl :: createUser : Error occurred while creating user: {}",
                    error.getMessage(),
                    error));
  }
}
