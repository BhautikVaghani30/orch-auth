package com.town.orch.auth.adaptor;

import static com.town.orch.auth.constants.TownErrorMessages.TECHNICAL_ERROR;

import com.town.common.model.response.CoreResponse;
import com.town.common.utils.CoreJsonParserUtils;
import com.town.orch.auth.constants.TownApiConstants;
import com.town.orch.auth.dto.TownUserProfileRequestDto;
import com.town.orch.auth.dto.TownUserProfileResponseDto;
import com.town.orch.auth.exception.CoreServiceException;
import com.town.orch.auth.intorceptore.LoggingInterceptor;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class TownOrchUserAdaptorImpl implements TownOrchUserAdaptor {

  private static final ParameterizedTypeReference<CoreResponse<TownUserProfileResponseDto>>
      getCoreResponseTypeReference = new ParameterizedTypeReference<>() {};

  private WebClient orchSocietyWebClient;

  private final LoggingInterceptor loggingInterceptor;

  @Value("${town.user.baseUrl}")
  private String townUserBaseUrl;

  @PostConstruct
  public void init() {
    orchSocietyWebClient = loggingInterceptor.getOrchWebClient(townUserBaseUrl, "orchUserService");
  }

  /**
   * Calls the society-processor to register a new society.
   *
   * @return
   */
  @Override
  public Mono<CoreResponse<TownUserProfileResponseDto>> createUserProfile(
      TownUserProfileRequestDto townUserProfileRequestDto) {
    if (log.isDebugEnabled()) {
      log.debug(
          "TownOrchUserAdaptor : createUserProfile :: townUserProfileRequestDto : {}",
          CoreJsonParserUtils.getJsonFromObject(townUserProfileRequestDto));
    }
    return orchSocietyWebClient
        .method(HttpMethod.POST)
        .uri(TownApiConstants.CREATE_USER_PROFILE_URL)
        .body(Mono.just(townUserProfileRequestDto), TownUserProfileRequestDto.class)
        .exchangeToMono(result -> result.bodyToMono(getCoreResponseTypeReference))
        .doOnSuccess(
            result -> {
              if (Objects.isNull(result) || result.getMessages().isEmpty()) {
                log.debug(
                    "TownOrchUserAdaptor : createUserProfile :: "
                        + "user profile creation failed with null or empty response : {}",
                    CoreJsonParserUtils.getJsonFromObject(result));
              }
              if (log.isDebugEnabled()) {
                log.debug(
                    "TownOrchUserAdaptorImpl : createUserProfile :: town user profile created result : {}",
                    CoreJsonParserUtils.getJsonFromObject(result));
              }
            })
        .onErrorMap(
            ex -> {
              if (ex instanceof CoreServiceException) {
                log.error(
                    "TownOrchUserAdaptor : createUserProfile :: A known service exception occurred during society registration: {}",
                    ((CoreServiceException) ex).getError().getCode(),
                    ex);
                return ex;
              }
              log.error(
                  "TownOrchUserAdaptor : createUserProfile :: An unexpected error occurred while calling the society-processor service.",
                  ex);
              return new CoreServiceException(TECHNICAL_ERROR);
            });
  }
}
