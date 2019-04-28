import dao.DaoInvitacion;
import dao.DaoNodo;
import dao.DaoPersona;
import dao.DaoProblematica;
import entity.Nodo;
import entity.Persona;
import filter.AuthFilter;
import health.PlantillaHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.postgres.PostgresPlugin;
import rest.*;
import usecase.FotoUseCase;
import util.JWTUtils;

public class App extends Application<ConfiguracionApp> {

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "App investigación";
    }

    @Override
    public void initialize(Bootstrap<ConfiguracionApp> bootstrap) {
        bootstrap.addBundle(new JdbiExceptionsBundle());
    }

    public void run(ConfiguracionApp configuracionApp, Environment environment) throws Exception {
        JWTUtils jwtUtils = new JWTUtils(configuracionApp.jwtKey);

        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuracionApp.getDataSourceFactory(), "postgres");
        jdbi.installPlugin(new PostgresPlugin());

        FotoUseCase fotoUseCase = new FotoUseCase(new DaoNodo(jdbi));

        final AuthFilter authFilter = new AuthFilter(jwtUtils);

        final AuthResource authResource = new AuthResource(jdbi.onDemand(DaoPersona.class), jwtUtils);

        final PersonaResource personaResource =
                new PersonaResource(jdbi.onDemand(DaoPersona.class), new DaoProblematica(jdbi), new DaoInvitacion(jdbi));

        final ProblematicaResource problematicaResource =
                new ProblematicaResource(new DaoProblematica(jdbi) , new DaoInvitacion(jdbi), fotoUseCase);

        final InvitacionResource invitacionResource =
                new InvitacionResource(new DaoInvitacion(jdbi));

        final NodoResource nodoResource = new NodoResource(fotoUseCase);

        final PlantillaHealthCheck plantillaCheck = new PlantillaHealthCheck(configuracionApp.getPlantilla());

        environment.jersey().register(MultiPartFeature.class);
        environment.healthChecks().register("template", plantillaCheck);
        environment.jersey().register(new JsonProcessingExceptionMapper(true));
        environment.jersey().register(authFilter);
        environment.jersey().register(authResource);
        environment.jersey().register(personaResource);
        environment.jersey().register(problematicaResource);
        environment.jersey().register(invitacionResource);
        environment.jersey().register(nodoResource);
    }
}