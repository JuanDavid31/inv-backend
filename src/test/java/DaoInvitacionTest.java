import dao.DaoInvitacion;
import entity.Invitacion;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DaoInvitacionTest {

    @ClassRule
    public static final DropwizardAppRule<ConfiguracionApp> RULE =
            new DropwizardAppRule<ConfiguracionApp>(App.class, ResourceHelpers.resourceFilePath("conf.yml"));

    static DaoInvitacion daoInvitacion;
    static Jdbi jdbi;

    @BeforeClass
    public static void preparacion(){
        final JdbiFactory factory = new JdbiFactory();
        jdbi = factory.build(RULE.getEnvironment(), RULE.getConfiguration().getDataSourceFactory(), "postgres");
        jdbi.installPlugin(new PostgresPlugin());
        daoInvitacion = new DaoInvitacion(jdbi);
    }

    @Before
    public void prepararDB(){
        jdbi.useHandle(handle -> handle.createUpdate("SELECT truncate_tables('postgres')").execute());
        jdbi.useHandle(handle -> handle.createUpdate("SELECT agregarDummyData()").execute());
    }

    @Test
    public void darPersonasInvitadas(){
        List<Invitacion> personas = daoInvitacion.darPersonasInvitadas("juan1@.com", 1);
        assertEquals(3, personas.size());
    }

    @Test
    public void darInvitacionesVigentes(){
        List<Map<String, Object>> invitaciones = daoInvitacion.darInvitacionesVigentes("juan4@.com");
        assertEquals(3, invitaciones.size());
    }

    @Test
    public void aceptarInvitacion(){
        boolean seAcepto = daoInvitacion.aceptarInvitacion(new Invitacion("",
                1,
                "juan1@.com",
                "juan4@.com",
                false,
                false));

        assertTrue(seAcepto);
    }

    @Test
    public void rechazarInvitacion(){
        boolean seRechazo = daoInvitacion.rechazarInvitacion(new Invitacion("",
                2,
                "david1@.com",
                "juan4@.com",
                false,
                false));

        assertTrue(seRechazo);
    }

    @Test
    public void eliminarInvitacion(){
        boolean seElimino = daoInvitacion.eliminarInvitacion(new Invitacion("",
                2,
                "david1@.com",
                "david4@.com",
                false,
                false));

        assertTrue(seElimino);
    }

    @Test
    public void agregarInvitacion(){
        Invitacion invitacion = daoInvitacion.agregarInvitacion(new Invitacion("",
                1,
                "juan1@.com",
                "david5@.com",
                true,
                false));

        assertNotNull(invitacion);
    }
}