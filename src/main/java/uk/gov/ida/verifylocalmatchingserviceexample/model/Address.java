package uk.gov.ida.verifylocalmatchingserviceexample.model;

public class Address {

    private final String postcode;

    public Address(String postcode) {
        this.postcode = postcode;
    }

    public String getPostcode() {
        return postcode;
    }
}
