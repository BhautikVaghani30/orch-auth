package com.town.orch.auth.model.service;

import com.town.orch.auth.dto.TownUserDTO;
import com.town.orch.auth.dto.TownUserProfileResponseDto;
import reactor.core.publisher.Mono;

public interface TownAuthService {
    Mono<TownUserProfileResponseDto> createUserProfile(TownUserDTO userDTO);
}
