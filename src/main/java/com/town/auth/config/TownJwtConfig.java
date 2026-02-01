/*
 * Copyright 2025 com.core.township
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law.log
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.town.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for JWT generation and validation. Binds values from the application's
 * configuration file (e.g., application.yml) to a structured, type-safe object.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class TownJwtConfig {

  /**
   * The secret key used for signing and verifying JWTs. Must be a strong, long, and random string.
   */
  private String secret;

  /** The validity duration of the JWT in milliseconds. Default is 10 hours. */
  private long expirationMs = 36000000L; // 10 hours
}
