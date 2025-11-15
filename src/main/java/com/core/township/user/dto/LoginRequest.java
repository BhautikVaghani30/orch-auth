// Place this in a new 'dto' or 'model' package, e.g., com.core.township.auth.dto
package com.core.township.user.dto;

// Place this in a new 'dto' or 'model' package, e.g., com.core.township.auth.dto

import com.core.township.user.constants.ValidationMessages;
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
    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.INVALID_EMAIL_FORMAT)
    private String email;

    /**
     * The user's password. It must meet strong complexity requirements.
     */
    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @Size(min = 8, max = 100, message = ValidationMessages.PASSWORD_LENGTH)
    @Pattern(
            regexp = ValidationMessages.PASSWORD_REGEX,
            message = ValidationMessages.PASSWORD_COMPLEXITY
    )
    private String password;
}