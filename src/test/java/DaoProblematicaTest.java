import dao.DaoProblematica;
import entity.Problematica;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static org.junit.Assert.*;

public class DaoProblematicaTest {

    @ClassRule
    public static final DropwizardAppRule<ConfiguracionApp> RULE =
            new DropwizardAppRule<ConfiguracionApp>(App.class, ResourceHelpers.resourceFilePath("conf.yml"));

    static DaoProblematica daoProblematica;
    static Jdbi jdbi;

    @BeforeClass
    public static void preparacion(){
        final JdbiFactory factory = new JdbiFactory();
        jdbi = factory.build(RULE.getEnvironment(), RULE.getConfiguration().getDataSourceFactory(), "postgres");
        jdbi.installPlugin(new PostgresPlugin());
        daoProblematica = new DaoProblematica(jdbi);
    }

    @Before
    public void prepararDB(){
        jdbi.useHandle(handle -> handle.createUpdate("SELECT truncate_tables('postgres')").execute());
        jdbi.useHandle(handle -> handle.createUpdate("SELECT agregarDummyData()").execute());
    }

    @Test
    public void agregarProblematicaYDarProblematicas(){
        Problematica problematica = daoProblematica.agregarProblematicaPorPersona("juan1@.com",
                new Problematica(0, "Problematica 3", "Descripcion 3"));
        assertNotNull(problematica);
    }

    @Test
    public void darProblematicas(){
        List<Problematica> problematicas = daoProblematica.darProblematicasPorPersona("juan1@.com");
        assertEquals(2, problematicas.size());
    }

    @Test
    public void avanzarFase(){
        boolean seAvanzo = daoProblematica.avanzarFaseProblematica(1);
        assertTrue(seAvanzo);
    }

    GenericContainer a;
}