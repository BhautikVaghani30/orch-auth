package com.town.orch.auth.adaptor;

import com.town.common.model.response.CoreResponse;
import com.town.orch.auth.dto.TownUserProfileRequestDto;
import com.town.orch.auth.dto.TownUserProfileResponseDto;
import reactor.core.publisher.Mono;

public interface TownOrchUserAdaptor {
  Mono<CoreResponse<TownUserProfileResponseDto>> createUserProfile(
          TownUserProfileRequestDto townUserProfileRequestDto);
}
