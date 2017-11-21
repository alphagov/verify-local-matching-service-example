package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.ida.verifylocalmatchingserviceexample.builders.AddressDtoBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressDtoTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldNotReturnConstraintViolationWhenFromDateAndVerificationIsNotNull() {
        AddressDto addressDto = new AddressDtoBuilder().build();

        Set<ConstraintViolation<AddressDto>> constraintViolations = validator.validate(addressDto);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    public void shouldReturnConstraintViolationWhenFromDateIsNull() {
        AddressDto addressDto = new AddressDtoBuilder().withFromDate(null).build();

        Set<ConstraintViolation<AddressDto>> constraintViolations = validator.validate(addressDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<AddressDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("fromDate", violation.getPropertyPath().toString());
    }

    @Test
    public void shouldReturnConstraintViolationWhenVerificationIsNull() {
        AddressDto addressDto = new AddressDtoBuilder().withVerified(null).build();

        Set<ConstraintViolation<AddressDto>> constraintViolations = validator.validate(addressDto);

        assertEquals(1, constraintViolations.size());
        ConstraintViolation<AddressDto> violation = constraintViolations.iterator().next();
        assertEquals("may not be null", violation.getMessage());
        assertEquals("verified", violation.getPropertyPath().toString());
    }

}