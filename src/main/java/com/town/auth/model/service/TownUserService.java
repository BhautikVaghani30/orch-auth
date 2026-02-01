package com.town.auth.model.service;

import com.town.auth.dto.TownUserDTO;
import com.town.auth.entities.TownUser;
import reactor.core.publisher.Mono;

public interface TownUserService {
    Mono<TownUser> createUser(TownUserDTO userDTO);
}
