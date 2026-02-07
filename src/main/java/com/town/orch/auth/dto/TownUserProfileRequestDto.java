package com.town.orch.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TownUserProfileRequestDto {
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
}
