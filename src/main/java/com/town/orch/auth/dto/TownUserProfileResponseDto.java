package com.town.orch.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.town.orch.auth.constants.TableConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

/**
 * Data Transfer Object for User Profile information. Used for API responses to decouple the API
 * layer from the persistence layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TownUserProfileResponseDto {

  /** Primary Key, stored as BINARY(16) in the database. */
  @Column(TableConstants.COLUMN_ID)
  private String id;

  /** The user's first name. */
  @Column(TableConstants.COLUMN_FIRST_NAME)
  private String firstName;

  /** The user's last name. */
  @Column(TableConstants.COLUMN_LAST_NAME)
  private String lastName;

  /** The user's email address. This should be unique across all users. */
  @Column(TableConstants.COLUMN_EMAIL)
  private String email;

  /** The user's phone number. */
  @Column(TableConstants.COLUMN_PHONE)
  private String phone;

  /** The user's phone number. */
  @Column(TableConstants.COUNTRY_CODE)
  private String countryCode;

  /** The user's phone number. */
  @Column(TableConstants.STATE)
  private String stateCode;

  /** The user's phone number. */
  @Column(TableConstants.CITY)
  private String city;

  /** The user's phone number. */
  @Column(TableConstants.LANDMARK)
  private String landmark;

  /**
   * The timestamp when the user account was created. This is typically managed by the database or
   * application logic upon creation.
   */
  @Column(TableConstants.COLUMN_CREATED_AT)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
  private String createdAt;

  /**
   * The timestamp when the user account was last updated. This is updated whenever a change is made
   * to the user's record.
   */
  @Column(TableConstants.COLUMN_UPDATED_AT)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
  private String updatedAt;
}
