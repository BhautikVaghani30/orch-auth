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

package com.town.orch.auth.controller;

import com.town.common.enums.CoreApiStatus;
import com.town.common.model.response.CoreMessage;
import com.town.common.model.response.CoreMeta;
import com.town.common.model.response.CoreResponse;
import com.town.common.utils.CoreJsonParserUtils;
import com.town.orch.auth.constants.TownErrorMessages;
import com.town.orch.auth.dto.TownUserDTO;
import com.town.orch.auth.dto.TownUserProfileResponseDto;
import com.town.orch.auth.entities.TownUser;
import com.town.orch.auth.model.api.TownUserProfileRegistrationAPI;
import com.town.orch.auth.model.service.TownAuthService;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * The TownUserController class is a REST controller that handles user-related HTTP requests. It
 * implements the {@link TownUserProfileRegistrationAPI} interface and provides endpoints for user
 * management, such as creating a new user. It uses {@link TownAuthService} to process business
 * logic.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class TownUserProfileProfileRegistrationController
    implements TownUserProfileRegistrationAPI {

  /**
   * Service for handling user-related business logic. It is injected via constructor by Lombok's
   * {@link RequiredArgsConstructor}.
   */
  private final TownAuthService townUserService;

  /**
   * Creates a new user based on the provided data. This method is part of the reactive stream and
   * handles the request asynchronously. It logs the request, calls the service to create the user,
   * and builds a successful response.
   *
   * @param user The {@link TownUserDTO} object containing the user's information.
   * @return A {@link Mono} emitting a {@link CoreResponse} with the created {@link TownUser}.
   */
  @Override
  public Mono<CoreResponse<TownUserProfileResponseDto>> createUser(TownUserDTO user) {
    return Mono.just(Optional.ofNullable(user))
        .doOnNext(
            userOpt ->
                userOpt.ifPresentOrElse(
                    u -> {
                      if (log.isDebugEnabled()) {
                        log.debug(
                            "TownUserProfileProfileRegistrationController :: createUser :: Request received to create user: {}",
                            CoreJsonParserUtils.getJsonFromObject(u));
                      }
                    },
                    () ->
                        log.debug(
                            "TownUserProfileProfileRegistrationController :: createUser :: Received empty request body to create user")))
        .flatMap(userOpt -> userOpt.map(townUserService::createUserProfile).orElse(Mono.empty()))
        .map(this::buildUserCreationSuccessfullyResponse)
        .doOnSuccess(
            response -> {
              if (log.isDebugEnabled()) {
                log.debug(
                    "TownUserProfileProfileRegistrationController :: createUser :: User successfully created: {}",
                    CoreJsonParserUtils.getJsonFromObject(response.getData()));
              }
            });
  }

  /**
   * Builds a standardized successful response for user creation.
   *
   * @param townUserProfileResponseDto The {@link TownUser} entity that was successfully created and
   *     saved.
   * @return A {@link CoreResponse} object containing the created user and success metadata.
   */
  private CoreResponse<TownUserProfileResponseDto> buildUserCreationSuccessfullyResponse(
      TownUserProfileResponseDto townUserProfileResponseDto) {
    CoreResponse<TownUserProfileResponseDto> response = new CoreResponse<>();
    response.setMeta(CoreMeta.builder().status(CoreApiStatus.SUCCESS.getValue()).build());
    response.setData(townUserProfileResponseDto);
    response.setMessages(
        Collections.singletonList(
            CoreMessage.builder().code(TownErrorMessages.USER_CREATED_SUCCESSFULLY).build()));
    return response;
  }
}
