package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDto {
    @JsonProperty("verified")
    @NotNull
    private Boolean verified;
    @JsonProperty
    @NotNull
    private DateTime fromDate;
    @JsonProperty
    private DateTime toDate;
    @JsonProperty("postCode")
    private String postCode;
    @JsonProperty("lines")
    private List<String> lines;
    @JsonProperty("internationalPostCode")
    private String internationalPostCode;
    @JsonProperty("uprn")
    private String uprn;

    public AddressDto() {

    }

    public AddressDto(Boolean verified,
                       DateTime fromDate,
                       DateTime toDate,
                       String postCode,
                       List<String> lines,
                       String internationalPostCode,
                      String uprn) {
        this.verified = verified;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.postCode = postCode;
        this.lines = lines;
        this.internationalPostCode = internationalPostCode;
        this.uprn = uprn;
    }

    public Boolean getVerified() {
        return verified;
    }

    public DateTime getFromDate() {
        return fromDate;
    }

    public DateTime getToDate() {
        return toDate;
    }

    public String getPostCode() {
        return postCode;
    }

    public List<String> getLines() {
        return lines;
    }

    public String getInternationalPostCode() {
        return internationalPostCode;
    }

    public String getUprn() {
        return uprn;
    }
}
