package uk.gov.ida.verifylocalmatchingserviceexample.builders;

import org.joda.time.DateTime;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.MatchingAttributesValueDto;

public class MatchingAttributesValueDtoBuilder<T> {

    private T value;
    private DateTime from = new DateTime().minusYears(1);
    private DateTime to = null;
    private Boolean verified = false;

    public MatchingAttributesValueDto<T> build() {
        return new MatchingAttributesValueDto(value, from, to, verified);
    }

    public static <T> MatchingAttributesValueDtoBuilder<T> aMatchingAttributesValueDtoBuilder() {
        return new MatchingAttributesValueDtoBuilder<>();
    }

    public MatchingAttributesValueDtoBuilder withValue(T value) {
        this.value = value;
        return this;
    }

    public MatchingAttributesValueDtoBuilder withFrom(DateTime from) {
        this.from = from;
        return this;
    }

    public MatchingAttributesValueDtoBuilder withTo(DateTime to) {
        this.to = to;
        return this;
    }

    public MatchingAttributesValueDtoBuilder withVerified(Boolean verified) {
        this.verified = verified;
        return this;
    }
}