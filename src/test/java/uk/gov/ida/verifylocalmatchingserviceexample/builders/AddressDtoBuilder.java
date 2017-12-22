package uk.gov.ida.verifylocalmatchingserviceexample.builders;

import org.joda.time.DateTime;
import uk.gov.ida.verifylocalmatchingserviceexample.contracts.AddressDto;

import java.util.List;

public class AddressDtoBuilder {
    private Boolean verified = true;
    private DateTime fromDate = DateTime.now();
    private DateTime toDate;
    private String postCode;
    private List<String> lines;
    private String internationalPostCode;
    private String uprn;

    public static AddressDtoBuilder anAddressDtoBuilder() {
        return new AddressDtoBuilder();
    }

    public AddressDto build() {
        return new AddressDto(verified, fromDate, toDate, postCode, lines, internationalPostCode, uprn);
    }

    public AddressDtoBuilder withVerified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    public AddressDtoBuilder withFromDate(DateTime fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public AddressDtoBuilder withToDate(DateTime toDate) {
        this.toDate = toDate;
        return this;
    }

    public AddressDtoBuilder withPostCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    public AddressDtoBuilder withLines(List<String> lines) {
        this.lines = lines;
        return this;
    }

    public AddressDtoBuilder withInternationalPostCode(String internationalPostCode) {
        this.internationalPostCode = internationalPostCode;
        return this;
    }

    public AddressDtoBuilder withUprn(String uprn) {
        this.uprn = uprn;
        return this;
    }
}
