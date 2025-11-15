/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.core.township.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration that will be common across all APIs */
@Configuration
public class TownOpenApiConfig {

  private static final String HEADER = "header";
  private static final String SCHEMA = "string";

  /**
   * Custom open api open api.
   *
   * @return the open api
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Township User Service API")
                .version("1.0.0")
                .description("API for managing users within the Township ecosystem.")
                .contact(new Contact().name("Township Support").email("support@township.com"))
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
//        .components(
//            new Components()
//                .addParameters(
//                    "country",
//                    new Parameter()
//                        .in(HEADER)
//                        .required(true)
//                        .schema(new Schema().type(SCHEMA))
//                        .style(Parameter.StyleEnum.FORM)
//                        .description("Profile country eg. US or CA")
//                        .name("X-ULTA-COUNTRY"))
//                .addParameters(
//                    "locale",
//                    new Parameter()
//                        .in(HEADER)
//                        .required(false)
//                        .schema(new Schema().type(SCHEMA))
//                        .style(Parameter.StyleEnum.FORM)
//                        .description("Profile language eg. en")
//                        .name("X-ULTA-LOCALE"))
//                .addParameters(
//                    "user-id",
//                    new Parameter()
//                        .in(HEADER)
//                        .required(true)
//                        .schema(new Schema().type(SCHEMA))
//                        .style(Parameter.StyleEnum.FORM)
//                        .description("Digital ID of the guest")
//                        .name("X-ULTA-USER-ID"))
//                .addParameters(
//                    "session-id",
//                    new Parameter()
//                        .in(HEADER)
//                        .required(true)
//                        .schema(new Schema().type(SCHEMA))
//                        .style(Parameter.StyleEnum.FORM)
//                        .description("The sessionId for user session.")
//                        .name("X-ULTA-SESSION-ID"))
//                .addParameters(
//                    "auth-status",
//                    new Parameter()
//                        .in(HEADER)
//                        .required(true)
//                        .schema(new Schema().type(SCHEMA))
//                        .style(Parameter.StyleEnum.FORM)
//                        .description("The auth status example - Hard, Soft, Anon")
//                        .name("X-ULTA-AUTH-STATUS")));
  }
}
