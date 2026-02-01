// Place this in a new 'dto' or 'model' package, e.g., com.core.township.auth.dto
package com.town.auth.dto;

// Place this in a new 'dto' or 'model' package, e.g., com.core.township.auth.dto

import com.town.auth.constants.TownErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor // Ensure a no-arg constructor exists
@AllArgsConstructor // Ensure an all-arg constructor exists
public class LoginRequest {

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
}