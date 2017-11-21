package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MatchStatusResponseDto {
    MATCH("match"),
    NO_MATCH("no-match");

    @JsonProperty(value = "result")
    private String result;

    MatchStatusResponseDto(String result) {
        this.result = result;
    }

    @JsonCreator
    public static MatchStatusResponseDto fromString(@JsonProperty(value = "result") String value) {
        return Arrays.stream(MatchStatusResponseDto.values())
                .filter(e -> e.result.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unknown match status %s.", value)));
    }

    public String getResult() {
        return result;
    }
}
