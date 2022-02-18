package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserCredentials{
	@JsonProperty("email")
	private String email;

	@JsonProperty("password")
	private String password;

}