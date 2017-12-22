package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesValueDtoBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.AddressDtoBuilder.anAddressDtoBuilder;
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesDtoBuilder.aMatchingAttributesDtoBuilder;

public class MatchingAttributesDtoTest {

    private static Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldNotReturnConstraintViolationWhenSurNamesAddressDateOfBirthIsPresent() {
        MatchingAttributesDto matchingAttributesDto = aMatchingAttributesDtoBuilder().build();

        Set<ConstraintViolation<MatchingAttributesDto>> constraintViolations = validator.validate(matchingAttributesDto);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void shouldReturnConstraintViolationWhenSurNamesIsNull() {
        MatchingAttributesDto matchingAttributesDto = aMatchingAttributesDtoBuilder()
                .withSurnames(null)
                .build();

        Set<ConstraintViolation<MatchingAttributesDto>> constraintViolations = validator.validate(matchingAttributesDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingAttributesDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be empty", violation.getMessage());
        assertEquals("surnames", violation.getPropertyPath().toString());
    }

    @Test
    public void shouldReturnConstraintViolationWhenAddressIsNull() {
        MatchingAttributesDto matchingAttributesDto = aMatchingAttributesDtoBuilder()
                .withAddresses(null)
                .build();

        Set<ConstraintViolation<MatchingAttributesDto>> constraintViolations = validator.validate(matchingAttributesDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingAttributesDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be empty", violation.getMessage());
        assertEquals("addresses", violation.getPropertyPath().toString());
    }

    @Test
    public void shouldReturnConstraintViolationWhenAddressIsMissingRequiredFields() {
        MatchingAttributesDto matchingAttributesDto = aMatchingAttributesDtoBuilder()
                .withAddresses(new LinkedList<AddressDto>() {{
                    add(anAddressDtoBuilder().withVerified(null).build());
                }})
                .build();

        Set<ConstraintViolation<MatchingAttributesDto>> constraintViolations = validator.validate(matchingAttributesDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingAttributesDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("addresses[0].verified", violation.getPropertyPath().toString());
    }

    @Test
    public void shouldReturnConstraintViolationWhenDateOfBirthIsNull() {
        MatchingAttributesDto matchingAttributesDto = aMatchingAttributesDtoBuilder()
                .withDateOfBirth(null)
                .build();

        Set<ConstraintViolation<MatchingAttributesDto>> constraintViolations = validator.validate(matchingAttributesDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingAttributesDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("dateOfBirth", violation.getPropertyPath().toString());
    }

    @Test
    public void shouldReturnConstraintViolationWhenDateOfBirthIsMissingRequiredFields() {
        MatchingAttributesDto matchingAttributesDto = aMatchingAttributesDtoBuilder()
                .withDateOfBirth(MatchingAttributesValueDtoBuilder.<LocalDate>aMatchingAttributesValueDtoBuilder().build())
                .build();

        Set<ConstraintViolation<MatchingAttributesDto>> constraintViolations = validator.validate(matchingAttributesDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingAttributesDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("dateOfBirth.value", violation.getPropertyPath().toString());
    }
}