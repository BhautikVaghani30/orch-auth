/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.town.orch.auth.intorceptore;

import com.town.common.config.CoreLocalDevelopmentConfig;
import com.town.common.webclient.CoreWebClientBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class LoggingInterceptor {

    @Value("${orch.auth.contentType}")
    private String contentType;

    @Value("${orch.auth.accept}")
    private String accept;

    @Autowired(required = false)
    private CoreLocalDevelopmentConfig coreLocalDevelopmentConfig;

    private final CoreWebClientBuilderService coreWebClientBuilderService;

    public WebClient getCoreWebClient(String coreCartBaseUrl, String clientName) {
        WebClient.Builder webClientBuilder =
                coreWebClientBuilderService
                        .createCoreWebClient(clientName)
                        .baseUrl(coreCartBaseUrl)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, contentType)
                        .defaultHeader(HttpHeaders.ACCEPT, accept)
                        .defaultHeader("X-apikey", "G5qMQZioRNVqPR7VffudCzGFPWVkHK9E")
                        .defaultHeader("User-Agent", "gomez")
                        .defaultHeader("Coresite", "en-US");

        if (coreLocalDevelopmentConfig != null) {
            coreLocalDevelopmentConfig.applyDefaultHeaders(webClientBuilder);
        }

        return webClientBuilder.build();
    }

    public WebClient getOrchWebClient(String orchProfileBaseUrl, String clientName) {
        WebClient.Builder webClientBuilder =
                coreWebClientBuilderService
                        .createOrchWebClient(clientName)
                        .baseUrl(orchProfileBaseUrl)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, contentType)
                        .defaultHeader(HttpHeaders.ACCEPT, accept)
                        .defaultHeader("X-apikey", "G5qMQZioRNVqPR7VffudCzGFPWVkHK9E")
                        .defaultHeader("User-Agent", "gomez")
                        .defaultHeader("ultasite", "en-US");

        if (coreLocalDevelopmentConfig != null) {
            coreLocalDevelopmentConfig.applyDefaultHeaders(webClientBuilder);

            // Applying any locally cached cookies to Orch WebClient requests.
            coreLocalDevelopmentConfig.applyLocalCookies(webClientBuilder);
        }

        return webClientBuilder.build();
    }

    public WebClient getExternalWebClient(String externalBaseUrl, String clientName) {
        WebClient.Builder webClientBuilder =
                coreWebClientBuilderService
                        .createExternalWebClient(clientName)
                        .baseUrl(externalBaseUrl)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, contentType)
                        .defaultHeader(HttpHeaders.ACCEPT, accept)
                        .defaultHeader("X-apikey", "G5qMQZioRNVqPR7VffudCzGFPWVkHK9E")
                        .defaultHeader("User-Agent", "gomez")
                        .defaultHeader("ultasite", "en-US");

        if (coreLocalDevelopmentConfig != null) {
            coreLocalDevelopmentConfig.applyDefaultHeaders(webClientBuilder);
        }

        return webClientBuilder.build();
    }
}