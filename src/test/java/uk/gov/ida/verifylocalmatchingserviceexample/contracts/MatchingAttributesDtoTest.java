package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

        System.out.println(constraintViolations);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void shouldReturnConstraintViolationWhenSurNamesIsNull() {
        MatchingAttributesDto matchingAttributesDto = aMatchingAttributesDtoBuilder()
                .withSurnames(null)
                .build();

        Set<ConstraintViolation<MatchingAttributesDto>> constraintViolations = validator.validate(matchingAttributesDto);

        System.out.println(constraintViolations);
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
    public void shouldReturnConstraintViolationWhenDateOfBirthIsNull() {
        MatchingAttributesDto matchingAttributesDto =aMatchingAttributesDtoBuilder()
                .withDateOfBirth(null)
                .build();

        Set<ConstraintViolation<MatchingAttributesDto>> constraintViolations = validator.validate(matchingAttributesDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingAttributesDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("dateOfBirth", violation.getPropertyPath().toString());
    }
}