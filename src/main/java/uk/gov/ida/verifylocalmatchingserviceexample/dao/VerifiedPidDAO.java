package uk.gov.ida.verifylocalmatchingserviceexample.dao;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

public interface VerifiedPidDAO {
    @SqlQuery("select pid from verifiedPid where pid=:pid")
    String getVerifiedPID(@Bind("pid") String pid);
}
