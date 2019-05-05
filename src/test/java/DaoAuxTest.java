import dao.DaoGrupo;
import dao.DaoInvitacion;
import dao.DaoReaccion;
import entity.Grupo;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

public class DaoAuxTest {

    @ClassRule
    public static final DropwizardAppRule<ConfiguracionApp> RULE =
            new DropwizardAppRule<ConfiguracionApp>(App.class, ResourceHelpers.resourceFilePath("conf.yml"));

    static DaoReaccion daoReaccion;
    static DaoGrupo daoGrupo;
    static Jdbi jdbi;

    @BeforeClass
    public static void preparacion(){
        final JdbiFactory factory = new JdbiFactory();
        jdbi = factory.build(RULE.getEnvironment(), RULE.getConfiguration().getDataSourceFactory(), "postgres");
        jdbi.installPlugin(new PostgresPlugin());
        daoReaccion = new DaoReaccion(jdbi);
        daoGrupo = new DaoGrupo(jdbi);
    }

    @Test
    public void testDarReacciones(){
        /*List<Grupo> grupos = daoGrupo.darGruposConReacciones(1);
        grupos.forEach(s -> System.out.println(s));*/
    }

    @Test
    public void testReaccionar(){
        boolean reaccionar = daoReaccion.reaccionar(0, 3, "juan1@.com1");
        System.out.println(reaccionar);
    }
}