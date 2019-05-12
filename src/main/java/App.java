import dao.*;
import entity.Nodo;
import entity.Persona;
import filter.AuthFilter;
import health.PlantillaHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.kotlin.KotlinPlugin;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.postgres.PostgresPlugin;
import rest.*;
import usecase.FotoUseCase;
import usecase.ProblematicaUseCase;
import util.CorreoUtils;
import util.JWTUtils;
import ws.InteraccionWebsocketServlet;

import javax.servlet.ServletRegistration;
import java.io.IOException;
import java.io.InputStream;

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
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)
        ));
    }

    public void run(ConfiguracionApp configuracionApp, Environment environment) throws Exception {
        ServletRegistration.Dynamic miServlet =
                environment.servlets().addServlet("miServlet", new InteraccionWebsocketServlet());
        miServlet.setAsyncSupported(true);
        miServlet.addMapping("/ws/*");


        System.out.println(configuracionApp.adminEmail);
        System.out.println(configuracionApp.adminPass);
        /*new CorreoUtils(configuracionApp.adminEmail, configuracionApp.adminPass)
                .enviar("juandavid0306@hotmail.com");*/

        JWTUtils jwtUtils = new JWTUtils(configuracionApp.jwtKey);

        //JDBI y plugins
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuracionApp.getDataSourceFactory(), "postgres");
        jdbi.installPlugin(new PostgresPlugin());
        jdbi.installPlugin(new KotlinPlugin());

        //Use cases
        FotoUseCase fotoUseCase = new FotoUseCase(new DaoNodo(jdbi));
        ProblematicaUseCase problematicaUseCase = new ProblematicaUseCase(new DaoProblematica(jdbi));

        //Filtros
        final AuthFilter authFilter = new AuthFilter(jwtUtils);

        //Resources
        final AuthResource authResource = new AuthResource(jdbi.onDemand(DaoPersona.class), jwtUtils);

        final PersonaResource personaResource =
                new PersonaResource(jdbi.onDemand(DaoPersona.class), new DaoProblematica(jdbi), new DaoInvitacion(jdbi), new DaoNodo(jdbi));

        final ProblematicaResource problematicaResource =
                new ProblematicaResource(new DaoProblematica(jdbi) , new DaoInvitacion(jdbi), new DaoGrupo(jdbi), fotoUseCase, problematicaUseCase);

        final InvitacionResource invitacionResource =
                new InvitacionResource(new DaoInvitacion(jdbi));

        final NodoResource nodoResource = new NodoResource(fotoUseCase, new DaoNodo(jdbi));

        //Healthchecks
        final PlantillaHealthCheck plantillaCheck = new PlantillaHealthCheck(configuracionApp.getPlantilla());

        //Registro de resouces y otras cosas
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