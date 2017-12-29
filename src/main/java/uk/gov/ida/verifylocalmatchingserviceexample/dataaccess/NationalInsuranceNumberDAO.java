package uk.gov.ida.verifylocalmatchingserviceexample.dataaccess;

import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class NationalInsuranceNumberDAO {

    private Jdbi jdbi;

    public NationalInsuranceNumberDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<Integer> getMatchingUserIds(List<Integer> personIds, String nationalInsuranceNumber) {
        return jdbi.withHandle(handle -> handle.createQuery("select person_id from " +
            "person left outer join nationalInsuranceNumber " +
            "on person.person_id = nationalInsuranceNumber.person_id " +
            "where person.person_id IN (<personIds>) and national_insurance_number = :nationalInsuranceNumber")
            .bind("personIds", personIds)
            .bind("nationalInsuranceNumber", nationalInsuranceNumber)
            .mapTo(Integer.class)
            .list());
    }
}
