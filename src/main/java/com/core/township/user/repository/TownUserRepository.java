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

package com.core.township.user.repository;

import com.core.township.user.entities.TownUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@link TownUser} entity.
 * It provides reactive, non-blocking methods for all CRUD operations.
 */
@Repository
public interface TownUserRepository extends ReactiveCrudRepository<TownUser, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email The email to search for.
     * @return A {@link Mono} emitting the user if found, or empty otherwise.
     */
    Mono<TownUser> findByEmail(String email);
}