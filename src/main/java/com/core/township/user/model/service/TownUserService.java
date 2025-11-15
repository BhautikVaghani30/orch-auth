package com.core.township.user.model.service;

import com.core.township.user.dto.TownUserDTO;
import com.core.township.user.entities.TownUser;
import reactor.core.publisher.Mono;

public interface TownUserService {
    Mono<TownUser> createUser(TownUserDTO userDTO);
}
