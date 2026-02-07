/*
 * Copyright 2025 com.core
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

package com.town.orch.auth.entities;

import com.town.orch.auth.constants.TableConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Represents a user entity in the system.
 * This class models the `users` table in the database and contains all user-related information.
 */
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = TableConstants.TABLE_USERS_ACCOUNT)
public class TownUser implements Persistable<String> {

    /** Primary Key, stored as BINARY(16) in the database. */
    @Id
    @Column("id")
    private String id;

    /**
     * The user's email address. This should be unique across all users.
     */
    @Column(TableConstants.COLUMN_EMAIL)
    private String email;

    /**
     * The user's hashed password. This field should never be exposed in API responses.
     */
    @Column(TableConstants.COLUMN_PASSWORD)
    private String password;

    /**
     * Flag to indicate if the user's account is active. Inactive users cannot log in.
     */
    @Column(TableConstants.COLUMN_IS_ACTIVE)
    private boolean isActive;

    /**
     * Flag to indicate if the user's email has been verified.
     */
    @Column(TableConstants.COLUMN_IS_VERIFIED)
    private boolean isVerified;

    /**
     * The timestamp when the user account was created.
     * This is typically managed by the database or application logic upon creation.
     */
    @Column(TableConstants.COLUMN_CREATED_AT)
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * The timestamp when the user account was last updated.
     * This is updated whenever a change is made to the user's record.
     */
    @Column(TableConstants.COLUMN_UPDATED_AT)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    @JsonIgnore
    @Transient
    public boolean isNew() {
        return id == null || createdAt == null;
    }
}
