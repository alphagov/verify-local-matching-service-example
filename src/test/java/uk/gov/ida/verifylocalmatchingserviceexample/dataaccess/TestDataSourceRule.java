package uk.gov.ida.verifylocalmatchingserviceexample.dataaccess;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.rules.ExternalResource;

public class TestDataSourceRule extends ExternalResource {
    private JdbcConnectionPool connectionPool;
    private Jdbi jdbi;

    @Override
    protected void before() throws Throwable {
        connectionPool = JdbcConnectionPool.create("jdbc:h2:mem:test;MODE=PostgreSQL", "", "");
        jdbi = Jdbi.create(connectionPool);
        Handle handle = jdbi.open();

        handle.begin();
        setUpDatabase(connectionPool);
        handle.commit();
    }

    private void setUpDatabase(JdbcConnectionPool connectionPool) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(connectionPool);
        flyway.setLocations("classpath:db.migration");
        flyway.migrate();
    }

    @Override
    protected void after() {
        connectionPool.dispose();
    }

    public Jdbi getJdbi() {
        return jdbi;
    }
}
