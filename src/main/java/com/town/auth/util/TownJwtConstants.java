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

package com.town.auth.util;

/**
 * Holds all constant values related to JWT operations.
 * This centralization prevents the use of magic strings and numbers in the business logic,
 * enhancing maintainability and code clarity.
 */
public final class TownJwtConstants {

    /** Private constructor to prevent instantiation of this utility class. */
    private TownJwtConstants() {}

    /** The key for the 'role' claim in the JWT payload. */
    public static final String ROLE_CLAIM = "role";
    /** The key for the 'sessionId' claim in the JWT payload, used for session tracking. */
    public static final String SESSION_ID_CLAIM = "sessionId";
    /** The key for the 'userId' claim in the JWT payload, linking the token to a user identifier. */
    public static final String USER_ID_CLAIM = "userId";
}