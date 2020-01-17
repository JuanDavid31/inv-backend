import dao.DaoGrupo;
import dao.DaoInvitacion;
import entity.Grupo;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import usecase.GrupoUseCase;
import usecase.InvitacionUseCase;

import java.util.List;

public class GrupoUseCaseTest {

    @ClassRule
    public static final DropwizardAppRule<ConfiguracionApp> RULE =
            new DropwizardAppRule<ConfiguracionApp>(App.class, ResourceHelpers.resourceFilePath("conf.yml"));

    static GrupoUseCase grupoUseCase;
    static Jdbi jdbi;

    @BeforeClass
    public static void preparacion(){
        final JdbiFactory factory = new JdbiFactory();
        jdbi = factory.build(RULE.getEnvironment(), RULE.getConfiguration().getDataSourceFactory(), "postgres");
        jdbi.installPlugin(new PostgresPlugin());
        DaoGrupo daoInvitacion = new DaoGrupo(jdbi);
        grupoUseCase = new GrupoUseCase(daoInvitacion, null, null);
    }

    @Before
    public void prepararDB(){
        jdbi.useHandle(handle -> handle.createUpdate("SELECT truncate_tables('postgres')").execute());
        jdbi.useHandle(handle -> handle.createUpdate("SELECT agregarDummyData()").execute());
    }

    @Test
    public void darGruposConReacciones(){
        List<Grupo> grupos = grupoUseCase.darGruposConReacciones(2);
        System.out.println("Tamaño gruposActivos - " + grupos.size());
        grupos.forEach(grupo -> System.out.println(grupo));
    }
}
