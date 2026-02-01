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

package com.town.auth.model.api;

import com.town.auth.constants.TownApiConstants;
import com.town.auth.constants.TownUserSwaggerPathConstant;
import com.town.auth.dto.TownUserDTO;
import com.town.auth.entities.TownUser;
import com.core.township.common.model.response.CoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * Defines the API for managing users within the Township ecosystem.
 * This interface outlines the contract for user-related operations, ensuring a consistent
 * and well-documented approach to user management.
 */
@RequestMapping(TownApiConstants.API_BASE_URL)
@Tag(name = "Town User API", description = "API for managing users within the Township ecosystem.")
public interface TownUserRegistrationAPI {

  /**
   * Creates a new user in the Township system.
   *
   * @param user The user object to be created.
   * @return A {@link Mono} containing a {@link ResponseEntity} with the created {@link TownUser}.
   */
  @PostMapping(
      path = TownApiConstants.CREATE_USER_URI,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Provides capabilities to create a new user",
      description = "This endpoint allows for the creation of a new user in the Township system.",
      responses = {
        @ApiResponse(
            description = "Successful Response",
            responseCode = "200",
            content = {
              @Content(
                  examples = {
                    @ExampleObject(
                        name = "HappyPath",
                        ref =
                            TownUserSwaggerPathConstant.CREATE_USER_FLOW_EXAMPLES
                                + TownUserSwaggerPathConstant.CREATE_USER),
                  },
                  mediaType = "application/json",
                  schema = @Schema(implementation = CoreResponse.class))
            })
      })
  Mono<CoreResponse<TownUser>> createUser(
      @Valid @RequestBody TownUserDTO user);
}
