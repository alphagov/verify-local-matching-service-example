package uk.gov.ida.verifylocalmatchingserviceexample.dataaccess;

import org.jdbi.v3.core.Jdbi;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NationalInsuranceNumberDAOTest {
    @Rule
    public TestDataSourceRule testDataSourceRule = new TestDataSourceRule();
    private Jdbi jdbi;

    @Before
    public void setUp() {
        jdbi = testDataSourceRule.getJdbi();
    }

    @Test
    public void shouldReturnPersonIdsForUsersMatchingGivenPidAndNationalInsuranceNumber() {
        NationalInsuranceNumberDAO nationalInsuranceNumberDAO = new NationalInsuranceNumberDAO(jdbi);

        createUser();
        int notMatchingPersonId = getLastInsertedPersonId();
        UpdateUserWithInsuranceNumber(notMatchingPersonId, "some-insurance-1");

        createUser();
        int matchingUserPersonId = getLastInsertedPersonId();
        UpdateUserWithInsuranceNumber(matchingUserPersonId, "some-insurance-number");

        List<Integer> matchingUserIds = nationalInsuranceNumberDAO
            .getMatchingUserIds(Arrays.asList(notMatchingPersonId, matchingUserPersonId), "some-insurance-number");

        assertEquals(Collections.singletonList(matchingUserPersonId), matchingUserIds);
    }

    @Test
    public void shouldReturnEmptyListWhenThereAreNoMatchingUsers() {
        NationalInsuranceNumberDAO nationalInsuranceNumberDAO = new NationalInsuranceNumberDAO(jdbi);

        List<Integer> matchingUserIds = nationalInsuranceNumberDAO
            .getMatchingUserIds(Arrays.asList(1,2,3), "some-insurance-number");

        assertTrue(matchingUserIds.isEmpty());
    }

    private void UpdateUserWithInsuranceNumber(int personId, String insuranceNumber) {
        jdbi.useHandle(handle ->
            handle.createUpdate("insert into nationalInsuranceNumber (national_insurance_number, person_id) values (:insuranceNumber, :personId)")
                .bind("personId", personId)
                .bind("insuranceNumber", insuranceNumber)
                .execute());
    }

    private void createUser() {
        jdbi.useHandle(handle ->
            handle.createUpdate("insert into person (surname) values (:surname)")
                .bind("surname", "some-surname")
                .execute());
    }

    public int getLastInsertedPersonId() {
        return jdbi.withHandle(handle ->
            handle.select("select MAX(person_id) FROM person").mapTo(Integer.class).findOnly()
        );
    }
}