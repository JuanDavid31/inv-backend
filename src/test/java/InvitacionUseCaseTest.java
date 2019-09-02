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
import usecase.InvitacionUseCase;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class InvitacionUseCaseTest {

    @ClassRule
    public static final DropwizardAppRule<ConfiguracionApp> RULE =
            new DropwizardAppRule<ConfiguracionApp>(App.class, ResourceHelpers.resourceFilePath("conf.yml"));

    static InvitacionUseCase invitacionUseCase;
    static Jdbi jdbi;

    @BeforeClass
    public static void preparacion(){
        final JdbiFactory factory = new JdbiFactory();
        jdbi = factory.build(RULE.getEnvironment(), RULE.getConfiguration().getDataSourceFactory(), "postgres");
        jdbi.installPlugin(new PostgresPlugin());
        DaoInvitacion daoInvitacion = new DaoInvitacion(jdbi);
        invitacionUseCase = new InvitacionUseCase(daoInvitacion);
    }

    @Before
    public void prepararDB(){
        jdbi.useHandle(handle -> handle.createUpdate("SELECT truncate_tables('postgres')").execute());
        jdbi.useHandle(handle -> handle.createUpdate("SELECT agregarDummyData()").execute());
    }

    @Test
    public void darPersonasInvitadas(){
        List<Invitacion> personas = invitacionUseCase.darPersonasInvitadas("juan1@.com", 1);
        assertEquals(9, personas.size());
    }

    @Test
    public void darPersonasInvitadasSinResultados(){
        List<Invitacion> invitaciones = invitacionUseCase.darPersonasInvitadas("juan3@.com", 1);
        assertEquals(0, invitaciones.size());
    }

    @Test
    public void darInvitacionesVigentes(){
        List<Map<String, Object>> invitaciones = invitacionUseCase.darInvitacionesVigentes("juan4@.com");
        assertEquals(2, invitaciones.size());
    }

    @Test
    public void aceptarInvitacion(){
        boolean seAcepto = invitacionUseCase.aceptarInvitacion(new Invitacion("",
                2,
                "david1@.com",
                "juan2@.com",
                true,
                false), "2david1@.comjuan2@.com");

        assertTrue(seAcepto);
    }

    @Test
    public void aceptarInvitacionConResultadoFalso() {
        boolean seAcepto = invitacionUseCase.aceptarInvitacion(new Invitacion("",
                    1,
                    "david1@.com",
                    "juan2@.com",
                    true,
                    false), "2david1@.comjuan2@.com");
        assertFalse(seAcepto);
    }

    @Test
    public void rechazarInvitacion(){
        boolean seRechazo = invitacionUseCase.rechazarInvitacion(new Invitacion("",
                2,
                "david1@.com",
                "juan2@.com",
                false,
                false), "2david1@.comjuan2@.com");

        assertTrue(seRechazo);
    }

    @Test
    public void rechazarInvitacionConResultadoFalso(){
        boolean seRechazo = invitacionUseCase.rechazarInvitacion(new Invitacion("",
                1,
                "david1@.com",
                "juan2@.com",
                false,
                false), "2david1@.comjuan2@.com");

        assertFalse(seRechazo);
    }

    @Test
    public void eliminarInvitacion(){
        boolean seElimino = invitacionUseCase.eliminarInvitacion(new Invitacion("",
                2,
                "david1@.com",
                "juan2@.com",
                false,
                false), "2david1@.comjuan2@.com");

        assertTrue(seElimino);
    }

    @Test
    public void eliminarInvitacionConResultadoFalso(){
        boolean seElimino = invitacionUseCase.eliminarInvitacion(new Invitacion("",
                1,
                "david1@.com",
                "juan2@.com",
                false,
                false), "2david1@.comjuan2@.com");

        assertFalse(seElimino);
    }

    @Test
    public void agregarInvitacion(){
        Invitacion invitacion = invitacionUseCase.hacerInvitacion(new Invitacion("",
                2,
                "juan1@.com",
                "david5@.com",
                true,
                false));

        assertNotNull(invitacion);
    }
}