package com.core.township.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(UserApplication.class);
        application.setWebApplicationType(WebApplicationType.REACTIVE);
        application.run(args);
	}

}
