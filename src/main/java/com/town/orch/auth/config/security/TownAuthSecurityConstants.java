/*
 * Copyright 2025 com.core.township
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.town.orch.auth.config.security;

import java.util.List;

/**
 * This class holds all constant values related to security configuration.
 * Centralizing constants helps in avoiding hardcoded strings and improves maintainability.
 */
public final class TownAuthSecurityConstants {


  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private TownAuthSecurityConstants() {}

  // --- Role Constants ---
  /** Role for building administrators. */
  public static final String ROLE_BUILDING_ADMIN = "BUILDING_ADMIN";
  /** Role for society administrators. */
  public static final String ROLE_SOCIETY_ADMIN = "SOCIETY_ADMIN";

  // --- Public API Path Patterns ---
  /** Path for user registration. */
  public static final String USER_REGISTRATION_PATH = "/api/v1/auth/signup";
  /** Path pattern for authentication-related endpoints. */
  public static final String AUTH_PATH_PATTERN = "/api/v1/auth/**";
  /** Path pattern for publicly accessible GET endpoints. */
  public static final String PUBLIC_API_PATH_PATTERN = "/api/v1/public/**";

  // --- Admin API Path Patterns ---
  /** Path pattern for building administration endpoints. */
  public static final String ADMIN_BUILDING_PATH_PATTERN = "/api/admin/building/**";
  /** Path pattern for society administration endpoints. */
  public static final String ADMIN_SOCIETY_PATH_PATTERN = "/api/admin/society/**";

  // --- CORS Configuration Constants ---
  /** The frontend URL allowed to make cross-origin requests. */
  public static final String ALLOWED_ORIGIN = "http://localhost:3000";
  /** The list of HTTP methods allowed for cross-origin requests. */
  public static final List<String> ALLOWED_METHODS = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
  /** The list of headers allowed in cross-origin requests. A wildcard allows all headers. */
  public static final List<String> ALLOWED_HEADERS = List.of("*");
  /** The path pattern for which this CORS configuration applies. */
  public static final String CORS_CONFIG_PATH_PATTERN = "/**";
  /** The name of the secure, HTTP-only cookie used to store the JWT authentication token. */
  public static final String AUTH_COOKIE_NAME = "TOWN_AT_TOKEN";

  public static final String HEALTH_CHECK_PATH = "/actuator/health";
}