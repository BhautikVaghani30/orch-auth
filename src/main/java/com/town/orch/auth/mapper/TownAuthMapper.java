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

package com.town.orch.auth.mapper;

import com.town.orch.auth.dto.TownUserDTO;
import com.town.orch.auth.entities.TownUser;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * An automated mapper interface for converting between User DTOs and Entities using MapStruct.
 *
 * <p>This component handles the boilerplate logic of transferring data, ensuring a clean separation
 * between the API layer (DTOs) and the persistence layer (Entities). The {@code componentModel =
 * "spring"} attribute makes the generated implementation a Spring bean, allowing it to be injected
 * into other services.
 */
@Mapper(
    componentModel = "spring",
    builder = @Builder(disableBuilder = true),
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TownAuthMapper {
  /**
   * Converts a {@link TownUserDTO} to a {@link TownUser} entity for creation.
   *
   * @param userDTO The user data transfer object.
   * @param encodedPassword The securely hashed password.
   * @return A {@link TownUser} entity ready to be persisted.
   */
  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
  @Mapping(target = "password", source = "encodedPassword")
  @Mapping(target = "active", expression = "java(true)")
  @Mapping(target = "verified", expression = "java(false)")
  @Mapping(
      target = "email",
      expression =
          "java(userDTO.getEmail() != null ? userDTO.getEmail().toLowerCase().trim() : null)")
  TownUser toEntity(TownUserDTO userDTO, String encodedPassword);
}
