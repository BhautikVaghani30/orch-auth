/*
 * Copyright 2025 com.core.township
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.town.auth.config.security;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A custom, immutable {@link UserDetails} implementation that encapsulates core user information,
 * including a unique user ID. This class promotes null safety by using {@link Optional} for all
 * nullable fields, ensuring that consumers handle potential absences of data gracefully.
 */
@Getter
@Setter
public class TownshipUserDetails implements UserDetails {

  /** The unique identifier for the user, essential for linking JWT claims back to a user record. */
  private final String userId;

  /** The username used for authentication, typically the user's email address. */
  private final String username;

  /** The user's securely encoded password. */
  private final String password;

  /** A list of authorities (roles/permissions) granted to the user. */
  private final List<SimpleGrantedAuthority> authorities;

  /**
   * Indicates whether the user's account has expired. An expired account cannot be authenticated.
   */
  private final boolean isAccountNonExpired;

  /** Indicates whether the user is locked or unlocked. A locked user cannot be authenticated. */
  private final boolean isAccountNonLocked;

  /** Indicates whether the user's credentials (password) has expired. */
  private final boolean isCredentialsNonExpired;

  /** Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated. */
  private final boolean isEnabled;

  /**
   * Constructs a new TownshipUserDetails object.
   *
   * @param userId The unique identifier for the user, which can be null.
   * @param username The username (email) used for authentication.
   * @param password The user's encoded password.
   * @param authorities The roles and permissions granted to the user.
   */
  public TownshipUserDetails(
          String userId, String username, String password, List<SimpleGrantedAuthority> authorities) {
    this.userId = userId;
    this.username = username;
    this.password = password;
    this.authorities = authorities != null ? authorities : Collections.emptyList();
    this.isAccountNonExpired = true;
    this.isAccountNonLocked = true;
    this.isCredentialsNonExpired = true;
    this.isEnabled = true;
  }

}
