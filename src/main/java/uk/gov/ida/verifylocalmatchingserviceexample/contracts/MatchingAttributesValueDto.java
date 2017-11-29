package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchingAttributesValueDto<T> {
    @JsonProperty("value")
    @NotNull
    private T value;
    @JsonProperty("from")
    private DateTime from;
    @JsonProperty("to")
    private DateTime to;
    @JsonProperty("verified")
    @NotNull
    private Boolean verified;

    public MatchingAttributesValueDto() {
    }

    public MatchingAttributesValueDto(T value,
                                      DateTime from,
                                      DateTime to,
                                      Boolean verified) {
        this.value = value;
        this.from = from;
        this.to = to;
        this.verified = verified;
    }

    public T getValue() {
        return value;
    }

    public DateTime getFrom() {
        return from;
    }

    public DateTime getTo() {
        return to;
    }

    public Boolean getVerified() {
        return verified;
    }
}
