package uk.gov.ida.verifylocalmatchingserviceexample.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseConfiguration {

    @NotNull
    @JsonProperty("url")
    private String url;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("password")
    private String password;

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
