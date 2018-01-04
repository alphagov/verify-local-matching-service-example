package uk.gov.ida.verifylocalmatchingserviceexample.builders;

import uk.gov.ida.verifylocalmatchingserviceexample.contracts.Cycle3AttributesDto;

import java.util.HashMap;
import java.util.Map;

public class Cycle3AttributesDtoBuilder {
    private Map<String, String> attributes = new HashMap<>();

    public static Cycle3AttributesDtoBuilder aCycle3AttributesDtoBuilder() {
        return new Cycle3AttributesDtoBuilder();
    }

    public Cycle3AttributesDto build() {
        return new Cycle3AttributesDto(attributes);
    }

    public Cycle3AttributesDtoBuilder withNationalInsuranceNumber(String nationalInsuranceNumber) {
        this.attributes.put("nationalInsuranceNumber", nationalInsuranceNumber);
        return this;
    }

    public Cycle3AttributesDtoBuilder withAttribute(String key, String value) {
        this.attributes.put(key, value);
        return this;
    }
}
