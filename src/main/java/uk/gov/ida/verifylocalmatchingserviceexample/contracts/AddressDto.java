package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDto {
    @NotNull
    private Boolean verified;
    @NotNull
    private DateTime fromDate;
    private DateTime toDate;
    private String postCode;
    private List<String> lines;
    private String internationalPostCode;
    private String uprn;

    @JsonCreator
    public AddressDto(@JsonProperty("verified") Boolean verified,
                      @JsonProperty("fromDate") DateTime fromDate,
                      @JsonProperty("toDate") DateTime toDate,
                      @JsonProperty("postCode") String postCode,
                      @JsonProperty("lines") List<String> lines,
                      @JsonProperty("internationalPostCode") String internationalPostCode,
                      @JsonProperty("uprn") String uprn) {
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
