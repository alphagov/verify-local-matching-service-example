package uk.gov.ida.verifylocalmatchingserviceexample.rules;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.rules.ExternalResource;
import uk.gov.ida.verifylocalmatchingserviceexample.configuration.VerifyLocalMatchingServiceExampleConfiguration;

public class H2JDBIRule extends ExternalResource {
    private Handle handle;
    private DropwizardAppRule<VerifyLocalMatchingServiceExampleConfiguration> appRule;

    public H2JDBIRule(DropwizardAppRule<VerifyLocalMatchingServiceExampleConfiguration> appRule) {
        this.appRule = appRule;
    }

    public Handle getHandle() {
        return handle;
    }

    @Override
    protected void before() throws Throwable {
        handle = Jdbi.create(this.appRule.getConfiguration().getDataSourceFactory().getUrl()).open();
        handle.begin();
        setUpDatabase();
        handle.commit();
    }

    @Override
    protected void after() {
        handle.close();
    }

    private void setUpDatabase() {
        handle.execute("create table verifiedPid(pid TEXT)");
    }

    public void ensurePidExist(String verifiedPid) {
        handle.begin();
        handle.execute("insert into verifiedPid values('" + verifiedPid + "')");
        handle.commit();
    }

    public void ensurePidDoesNotExist(String verifiedPid) {
        handle.begin();
        handle.execute("delete from verifiedPid where pid =('" + verifiedPid + "')");
        handle.commit();
    }

}
