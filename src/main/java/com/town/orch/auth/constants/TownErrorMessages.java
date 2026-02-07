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

package com.town.orch.auth.constants;


/**
 * A final class to hold all validation message constants for DTOs.
 *
 * <p>Centralizing validation messages prevents hardcoding strings, ensures consistency, and
 * simplifies maintenance and internationalization efforts.
 */
public final class TownErrorMessages {

  /** Private constructor to prevent instantiation. */
  private TownErrorMessages() {}

  public static final String TECHNICAL_ERROR = "TECHNICAL_ERROR";

  // User DTO Messages
  public static final String FIRST_NAME_REQUIRED = "E_R_1001_Town_User_First_Name_Required";
  public static final String LAST_NAME_REQUIRED = "E_R_1002_Town_User_Last_Name_Required";
  public static final String EMAIL_REQUIRED = "E_R_1003_Town_User_Email_Required";
  public static final String INVALID_EMAIL_FORMAT = "E_R_1004_Town_User_Invalid_Email_Format";
  public static final String PASSWORD_REQUIRED = "E_R_1005_Town_User_Password_Required";
  public static final String PASSWORD_LENGTH = "E_R_1006_Town_User_Password_Length";
  public static final String PASSWORD_COMPLEXITY = "E_R_1007_Town_User_Password_Complexity";
  public static final String INVALID_PHONE_FORMAT = "E_R_1008_Town_User_Invalid_Phone_Format";

  // Regular Expressions
  public static final String PASSWORD_REGEX =
      "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
  public static final String PHONE_REGEX = "^\\+?[0-9]{7,15}$";

  // user creation
  public static final String USER_ALREADY_EXISTS = "E_R_1009_Town_User_Already_Exists";
  public static final String USER_CREATED_SUCCESSFULLY = "S_R_1010_Town_User_Created_Successfully";

  public static final String USER_NOT_FOUND = "E_R_1011_Town_User_Not_Found";
  public static final String CREDENTIALS_REQUIRED = "E_R_1012_Town_User_Credentials_Required";
  public static final String UNAUTHORIZED = "E_R_1013_Town_Unauthorized_User";
  public static final String AUTHENTICATED = "S_R_1014_Town_User_Authorization_Successfully";
}
