package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape= JsonFormat.Shape.OBJECT)
public enum MatchStatusResponseDto {
    MATCH("match"),
    NO_MATCH("no-match");

    @JsonProperty(value = "result")
    private String result;

    MatchStatusResponseDto(String result) {
        this.result = result;
    }
}
