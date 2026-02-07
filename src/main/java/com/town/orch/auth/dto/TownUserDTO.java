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

package com.town.orch.auth.dto;

import com.town.orch.auth.constants.TownErrorMessages;
import com.town.orch.auth.entities.TownUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the data transfer object for creating a new user.
 * This class is used to capture and validate user input from the API
 * before it is processed and converted into a {@link TownUser} entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TownUserDTO {

    /**
     * The user's first name.
     * It cannot be null or empty.
     */
    @NotBlank(message = TownErrorMessages.FIRST_NAME_REQUIRED)
    private String firstName;

    /**
     * The user's last name.
     * It cannot be null or empty.
     */
    @NotBlank(message = TownErrorMessages.LAST_NAME_REQUIRED)
    private String lastName;

    /**
     * The user's email address.
     * It must be a valid email format and cannot be null or empty.
     */
    @NotBlank(message = TownErrorMessages.EMAIL_REQUIRED)
    @Email(message = TownErrorMessages.INVALID_EMAIL_FORMAT)
    private String email;

    /**
     * The user's password. It must meet strong complexity requirements.
     */
    @NotBlank(message = TownErrorMessages.PASSWORD_REQUIRED)
    @Size(min = 8, max = 100, message = TownErrorMessages.PASSWORD_LENGTH)
    @Pattern(
            regexp = TownErrorMessages.PASSWORD_REGEX,
            message = TownErrorMessages.PASSWORD_COMPLEXITY
    )
    private String password;

    /**
     * The user's phone number.
     * This field is optional. If provided, it must be a valid format
     * containing 7 to 15 digits, with an optional '+' prefix.
     */
    @Pattern(
            regexp = TownErrorMessages.PHONE_REGEX,
            message = TownErrorMessages.INVALID_PHONE_FORMAT
    )
    private String phone;
}
