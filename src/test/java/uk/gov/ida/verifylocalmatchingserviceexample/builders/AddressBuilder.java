package uk.gov.ida.verifylocalmatchingserviceexample.builders;

import uk.gov.ida.verifylocalmatchingserviceexample.model.Address;

public class AddressBuilder {

    private String postcode = "default-postcode";

    private AddressBuilder() {
        // prevents from using new
    }

    public static AddressBuilder anAddress() {
        return new AddressBuilder();
    }

    public static Address anyAddress() {
        return new AddressBuilder().build();
    }

    public Address build() {
        return new Address(postcode);
    }

    public AddressBuilder withPostcode(String postcode) {
        this.postcode = postcode;
        return this;
    }
}
