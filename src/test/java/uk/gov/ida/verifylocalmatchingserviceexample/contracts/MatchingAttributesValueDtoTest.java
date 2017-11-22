package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingAttributesValueDtoBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MatchingAttributesValueDtoTest {
    private static Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldNotReturnConstraintViolationWhenValueAndVerifiedIsSet() {
        MatchingAttributesValueDto<String> matchingAttributesValue = MatchingAttributesValueDtoBuilder
                .<String>aMatchingAttributesValueDtoBuilder()
                .withValue("random-value")
                .build();

        Set<ConstraintViolation<MatchingAttributesValueDto<String>>> constraintViolations = validator.validate(matchingAttributesValue);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void shouldReturnConstraintViolationWhenValueIsNull() {
        MatchingAttributesValueDto<String> matchingAttributesValue = MatchingAttributesValueDtoBuilder
                .<String>aMatchingAttributesValueDtoBuilder()
                .build();

        Set<ConstraintViolation<MatchingAttributesValueDto<String>>> constraintViolations =
                validator.validate(matchingAttributesValue);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingAttributesValueDto<String>> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("value", violation.getPropertyPath().toString());
    }

    @Test
    public void shouldReturnConstraintViolationWhenVerifiedIsNull() {
        MatchingAttributesValueDto<String> matchingAttributesValue = MatchingAttributesValueDtoBuilder
                .<String>aMatchingAttributesValueDtoBuilder()
                .withValue("random-value")
                .withVerified(null)
                .build();

        Set<ConstraintViolation<MatchingAttributesValueDto<String>>> constraintViolations = validator.validate(matchingAttributesValue);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingAttributesValueDto<String>> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("verified", violation.getPropertyPath().toString());
    }

}