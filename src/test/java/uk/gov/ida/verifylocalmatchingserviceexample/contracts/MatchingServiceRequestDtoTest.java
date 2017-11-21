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
import static uk.gov.ida.verifylocalmatchingserviceexample.builders.MatchingServiceRequestDtoBuilder.aMatchingServiceRequestDtoBuilder;

public class MatchingServiceRequestDtoTest {

    private static Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldNotHaveConstraintViolationWhenRequiredAttributesArePresent() {
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .build();

        Set<ConstraintViolation<MatchingServiceRequestDto>> constraintViolations = validator.validate(matchingServiceRequestDto);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void shouldReturnConstraintViolationWhenMatchingAttributesDtoIsNull() {
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .withMatchingAttributesDto(null)
                .build();

        Set<ConstraintViolation<MatchingServiceRequestDto>> constraintViolations = validator.validate(matchingServiceRequestDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingServiceRequestDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("matchingAttributesDto", violation.getPropertyPath().toString());
    }

    @Test
    public void shouldReturnConstraintViolationWhenMatchingAttributesDtoIsMissingRequiredFields() {
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .withMatchingAttributesDto(aMatchingAttributesDtoBuilder().withDateOfBirth(null).build())
                .build();

        Set<ConstraintViolation<MatchingServiceRequestDto>> constraintViolations = validator.validate(matchingServiceRequestDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingServiceRequestDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("matchingAttributesDto.dateOfBirth", violation.getPropertyPath().toString());
    }

    @Test
    public void shouldReturnConstraintViolationWhenHashedPidIsNull() {
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .withHashedPid(null)
                .build();

        Set<ConstraintViolation<MatchingServiceRequestDto>> constraintViolations = validator.validate(matchingServiceRequestDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingServiceRequestDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be empty", violation.getMessage());
        assertEquals("hashedPid", violation.getPropertyPath().toString());
    }

    @Test
    public void shouldReturnConstraintViolationWhenMatchIdIsNull() {
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .withMatchId(null)
                .build();

        Set<ConstraintViolation<MatchingServiceRequestDto>> constraintViolations = validator.validate(matchingServiceRequestDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingServiceRequestDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be empty", violation.getMessage());
        assertEquals("matchId", violation.getPropertyPath().toString());
    }

    @Test
    public void shouldReturnConstraintViolationWhenLevelOfAssuranceIsNull() {
        MatchingServiceRequestDto matchingServiceRequestDto = aMatchingServiceRequestDtoBuilder()
                .withLevelOfAssuranceDto(null)
                .build();

        Set<ConstraintViolation<MatchingServiceRequestDto>> constraintViolations = validator.validate(matchingServiceRequestDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<MatchingServiceRequestDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("levelOfAssurance", violation.getPropertyPath().toString());
    }

}