import dao.DaoInvitacion;
import dao.DaoProblematica;
import entity.Problematica;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DaoProblematicaTest {

    @ClassRule
    public static final DropwizardAppRule<ConfiguracionApp> RULE =
            new DropwizardAppRule<ConfiguracionApp>(App.class, ResourceHelpers.resourceFilePath("conf.yml"));

    static DaoProblematica daoProblematica;

    @BeforeClass
    public static void preparacion(){
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(RULE.getEnvironment(), RULE.getConfiguration().getDataSourceFactory(), "postgres");
        jdbi.installPlugin(new PostgresPlugin());
        daoProblematica = new DaoProblematica(jdbi);
        jdbi.useHandle(handle -> handle.createUpdate("SELECT truncate_tables('postgres')").execute());
        jdbi.useHandle(handle -> handle.createUpdate("SELECT agregarDummyData()").execute());
    }

    @Test
    public void agregarProblematicaYDarProblematicas(){
        Problematica problematica = daoProblematica.agregarProblematicaPorPersona("juan1@.com",
                new Problematica(0, "Problematica 3", "Descripcion 3"));
        assertNotNull(problematica);

        List<Problematica> problematicas = daoProblematica.darProblematicasPorPersona("juan1@.com");
        assertEquals(3, problematicas.size());
    }
}
